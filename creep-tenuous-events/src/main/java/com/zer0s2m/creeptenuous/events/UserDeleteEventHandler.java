package com.zer0s2m.creeptenuous.events;

import com.zer0s2m.creeptenuous.common.utils.OptionalMutable;
import com.zer0s2m.creeptenuous.common.query.IFileObjectsExclusions;
import com.zer0s2m.creeptenuous.common.utils.WalkDirectoryInfo;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.models.user.UserSettings;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.services.resources.ServiceRedisManagerResources;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceControlUserRights;
import com.zer0s2m.creeptenuous.repository.user.UserFileObjectsExclusionRepository;
import com.zer0s2m.creeptenuous.repository.user.UserSettingsRepository;
import com.zer0s2m.creeptenuous.services.system.ServiceDeleteDirectory;
import com.zer0s2m.creeptenuous.services.system.ServiceDeleteFile;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDeleteDirectoryImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDeleteFileImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Performs the following actions when deleting a user:
 * <ul>
 *     <li>Removing all file system objects from storage</li>
 *     <li>Removing objects from Redis</li>
 *     <li>Remove all assigned and granted user rights</li>
 *     <li>Remove jwt tokens user</li>
 *     <li>Removing all file system objects from storage</li>
 * </ul>
 */
@Component
class UserDeleteEventHandler implements ApplicationListener<UserDeleteEvent> {

    private final ServiceControlUserRights serviceControlUserRights;

    private final ServiceRedisManagerResources serviceRedisManagerResources;

    private final UserSettingsRepository userSettingsRepository;

    private final UserFileObjectsExclusionRepository userFileObjectsExclusionRepository;

    private final ServiceDeleteDirectory serviceDeleteDirectory = new ServiceDeleteDirectoryImpl();

    private final ServiceDeleteFile serviceDeleteFile = new ServiceDeleteFileImpl();

    @Autowired
    public UserDeleteEventHandler(
            ServiceControlUserRights serviceControlUserRights,
            ServiceRedisManagerResources serviceRedisManagerResources,
            UserSettingsRepository userSettingsRepository,
            UserFileObjectsExclusionRepository userFileObjectsExclusionRepository) {
        this.serviceControlUserRights = serviceControlUserRights;
        this.serviceRedisManagerResources = serviceRedisManagerResources;
        this.userSettingsRepository = userSettingsRepository;
        this.userFileObjectsExclusionRepository = userFileObjectsExclusionRepository;
    }

    /**
     * Handle an application event.
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(@NotNull UserDeleteEvent event) {
        final String userLogin = event.userLogin();

        AtomicBoolean isDeleting = new AtomicBoolean(true);

        Optional<UserSettings> userSettingsRaw = userSettingsRepository.findByUser_Login(userLogin);
        OptionalMutable<User> transferredUserRaw = new OptionalMutable<>(null);
        if (userSettingsRaw.isPresent()) {
            UserSettings userSettings = userSettingsRaw.get();
            isDeleting.set(userSettings.getIsDeletingFileObjects());
            transferredUserRaw.setValue(userSettings.getTransferredUser());
        }

        serviceControlUserRights.removeJwtTokensFotUser(userLogin);

        if (isDeleting.get() || (!isDeleting.get() && transferredUserRaw.isEmpty())) {
            deleteFiles(userLogin);
            deleteDirectories(userLogin);

            serviceControlUserRights.removeAssignedPermissionsForUser(userLogin);
            serviceControlUserRights.removeGrantedPermissionsForUser(userLogin);
            serviceControlUserRights.removeFileSystemObjects(userLogin);
        } else if (transferredUserRaw.getValue() != null) {
            IFileObjectsExclusions fileObjectsExclusions = getFileObjectsExclusions(userLogin);
            if (fileObjectsExclusions != null) {
                List<UUID> fileSystemObjectsExclusions;
                try {
                    fileSystemObjectsExclusions = getNamesFileSystemObjectExclusions(
                            getPathsDirectoryInExclusions(fileObjectsExclusions));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                deleteFilesBySystemNames(userLogin, fileSystemObjectsExclusions);
                deleteDirectoriesBySystemNames(userLogin, fileSystemObjectsExclusions);

                // Setting a parameter for distribution after stinging files from exclusions
                serviceControlUserRights.setFileObjectsExclusions(fileSystemObjectsExclusions);
                serviceControlUserRights.setIsDistribution(true);
                serviceControlUserRights.removeAssignedPermissionsForUser(userLogin);
                serviceControlUserRights.removeGrantedPermissionsForUser(userLogin);
                serviceControlUserRights.removeFileSystemObjectsBySystemNames(userLogin,
                        fileSystemObjectsExclusions);
            }

            serviceControlUserRights.setFileObjectsExclusions(null);
            serviceControlUserRights.setIsDistribution(false);
            User transferredUser = transferredUserRaw.getValue();
            serviceControlUserRights.deleteAssignedPermissionsForUser(userLogin, transferredUser.getLogin());
            serviceControlUserRights.migrateAssignedPermissionsForUser(userLogin, transferredUser.getLogin());
            serviceControlUserRights.removeAssignedPermissionsForUser(userLogin);
            serviceControlUserRights.migrateFileSystemObjects(userLogin, transferredUser.getLogin());
        }
    }

    /**
     * Get system directory paths from user exclusion
     * @param fileObjectsExclusions projection to get file objects that are in exclusions and aggregate by users
     * @return system directory path
     */
    private @NotNull List<Path> getPathsDirectoryInExclusions(
            @NotNull IFileObjectsExclusions fileObjectsExclusions) {
        List<Path> pathsDirectoryInExclusions = new ArrayList<>();
        List<String> fileSystemObjectsStr = fileObjectsExclusions.getFileSystemObjects()
                .stream()
                .map(UUID::toString)
                .toList();
        List<DirectoryRedis> directoryRedisList = serviceRedisManagerResources
                .getResourceDirectoryRedisByLoginUser(fileObjectsExclusions.getUserLogin());

        for (DirectoryRedis directoryRedis : directoryRedisList) {
            if (fileSystemObjectsStr.contains(directoryRedis.getSystemName())) {
                pathsDirectoryInExclusions.add(Path.of(directoryRedis.getPath()));
            }
        }

        return pathsDirectoryInExclusions;
    }

    /**
     * Get the system names of file objects that are in exclusions using the directory iteration method
     * @param paths system paths of file objects
     * @return system names of file objects that are in exclusions
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     */
    private @NotNull List<UUID> getNamesFileSystemObjectExclusions(@NotNull List<Path> paths) throws IOException {
        Set<String> systemNames = new HashSet<>();

        for (Path path : paths) {
            systemNames.addAll(
                    WalkDirectoryInfo.getNamesFileSystemObject(path));
            systemNames.add(path.getFileName().toString());
        }

        return systemNames
                .stream()
                .map(UUID::fromString)
                .toList();
    }

    /**
     * Delete all files of user
     * @param userLogin user login
     */
    private void deleteDirectories(String userLogin) {
        List<Path> directoryPaths = new ArrayList<>();

        serviceRedisManagerResources
                .getResourceDirectoryRedisByLoginUser(userLogin)
                .forEach(directoryRedis -> directoryPaths.add(Path.of(directoryRedis.getPath())));

        deleteDirectory(directoryPaths);
    }

    /**
     * Delete all directories of user by systemNames
     * @param userLogin user login. Must not be {@literal null}.
     * @param systemNames system names of file objects. Must not contain {@literal null} elements.
     */
    private void deleteDirectoriesBySystemNames(String userLogin, Collection<UUID> systemNames) {
        List<Path> directoryPaths = new ArrayList<>();

        serviceRedisManagerResources
                .getResourceDirectoryRedisByLoginUserAndInSystemNames(userLogin, systemNames)
                .forEach(directoryRedis -> directoryPaths.add(Path.of(directoryRedis.getPath())));

        deleteDirectory(directoryPaths);
    }

    /**
     * Deleting a directory by its path
     * @param paths directory paths
     */
    private void deleteDirectory(@NotNull List<Path> paths) {
        paths.forEach(source -> {
            if (Files.exists(source)) {
                try {
                    serviceDeleteDirectory.delete(source);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Delete all directories user of
     * @param userLogin user login
     */
    private void deleteFiles(String userLogin) {
        List<Path> filePaths = new ArrayList<>();

        serviceRedisManagerResources
                .getResourceFileRedisByLoginUser(userLogin)
                .forEach(fileRedis -> filePaths.add(Path.of(fileRedis.getPath())));

        deleteFile(filePaths);
    }

    /**
     * Delete all files of user by systemNames
     * @param userLogin user login. Must not be {@literal null}.
     * @param systemNames system names of file objects. Must not contain {@literal null} elements.
     */
    private void deleteFilesBySystemNames(String userLogin, Collection<UUID> systemNames) {
        List<Path> filePaths = new ArrayList<>();

        serviceRedisManagerResources
                .getResourceFileRedisByLoginUserAndInSystemNames(userLogin, systemNames)
                .forEach(fileRedis -> filePaths.add(Path.of(fileRedis.getPath())));

        deleteFile(filePaths);
    }

    /**
     * Deleting a file by its path
     * @param paths file paths
     */
    private void deleteFile(@NotNull List<Path> paths) {
        paths.forEach(source -> {
            if (Files.exists(source)) {
                try {
                    serviceDeleteFile.delete(source);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Find all file objects that are in exclusions by user login
     * @param userLogin user login
     * @return entity projection
     */
    private IFileObjectsExclusions getFileObjectsExclusions(final String userLogin) {
        return userFileObjectsExclusionRepository
                .getAllExclusionsAggregateUserId()
                .stream()
                .filter(customer -> userLogin.equals(customer.getUserLogin()))
                .findAny()
                .orElse(null);
    }

}
