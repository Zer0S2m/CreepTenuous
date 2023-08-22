package com.zer0s2m.creeptenuous.events;

import com.zer0s2m.creeptenuous.common.utils.OptionalMutable;
import com.zer0s2m.creeptenuous.common.query.IFileObjectsExclusions;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.models.user.UserSettings;
import com.zer0s2m.creeptenuous.redis.services.resources.ServiceRedisManagerResources;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceControlUserRights;
import com.zer0s2m.creeptenuous.repository.user.UserFileObjectsExclusionRepository;
import com.zer0s2m.creeptenuous.repository.user.UserSettingsRepository;
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

    private final ServiceDeleteDirectoryImpl serviceDeleteDirectory;

    private final ServiceDeleteFileImpl serviceDeleteFile;

    @Autowired
    public UserDeleteEventHandler(
            ServiceControlUserRights serviceControlUserRights,
            ServiceRedisManagerResources serviceRedisManagerResources,
            UserSettingsRepository userSettingsRepository,
            UserFileObjectsExclusionRepository userFileObjectsExclusionRepository,
            ServiceDeleteDirectoryImpl serviceDeleteDirectory,
            ServiceDeleteFileImpl serviceDeleteFile) {
        this.serviceControlUserRights = serviceControlUserRights;
        this.serviceRedisManagerResources = serviceRedisManagerResources;
        this.userSettingsRepository = userSettingsRepository;
        this.userFileObjectsExclusionRepository = userFileObjectsExclusionRepository;
        this.serviceDeleteDirectory = serviceDeleteDirectory;
        this.serviceDeleteFile = serviceDeleteFile;
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
                deleteFilesBySystemNames(
                        userLogin, fileObjectsExclusions.getFileSystemObjects());
                deleteDirectoriesBySystemNames(
                        userLogin, fileObjectsExclusions.getFileSystemObjects());

                // Setting a parameter for distribution after stinging files from exclusions
                serviceControlUserRights.setFileObjectsExclusions(fileObjectsExclusions.getFileSystemObjects());
                serviceControlUserRights.setIsDistribution(true);
                serviceControlUserRights.removeAssignedPermissionsForUser(userLogin);
                serviceControlUserRights.removeGrantedPermissionsForUser(userLogin);
                serviceControlUserRights.removeFileSystemObjectsBySystemNames(
                        userLogin, fileObjectsExclusions.getFileSystemObjects());
            }

            User transferredUser = transferredUserRaw.getValue();
            serviceControlUserRights.migrateAssignedPermissionsForUser(userLogin, transferredUser.getLogin());
            serviceControlUserRights.migrateFileSystemObjects(userLogin, transferredUser.getLogin());
            serviceControlUserRights.deleteAssignedPermissionsForUser(userLogin, transferredUser.getLogin());
        }
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
