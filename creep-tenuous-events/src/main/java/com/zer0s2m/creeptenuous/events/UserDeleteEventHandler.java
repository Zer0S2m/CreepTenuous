package com.zer0s2m.creeptenuous.events;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.resources.ServiceRedisManagerResources;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceControlUserRights;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDeleteDirectoryImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDeleteFileImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

    private final ServiceDeleteDirectoryImpl serviceDeleteDirectory;

    private final ServiceDeleteFileImpl serviceDeleteFile;

    @Autowired
    public UserDeleteEventHandler(
            ServiceControlUserRights serviceControlUserRights,
            ServiceRedisManagerResources serviceRedisManagerResources,
            ServiceDeleteDirectoryImpl serviceDeleteDirectory,
            ServiceDeleteFileImpl serviceDeleteFile) {
        this.serviceControlUserRights = serviceControlUserRights;
        this.serviceRedisManagerResources = serviceRedisManagerResources;
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

        deleteDirectories(userLogin);
        deleteFiles(userLogin);

        serviceControlUserRights.removeAssignedPermissionsForUser(userLogin);
        serviceControlUserRights.removeGrantedPermissionsForUser(userLogin);
        serviceControlUserRights.removeFileSystemObjects(userLogin);
        serviceControlUserRights.removeJwtTokensFotUser(userLogin);
    }

    /**
     * Delete all files of user
     * @param userLogin user login
     */
    private void deleteFiles(String userLogin) {
        List<Path> directoryPaths = new ArrayList<>();

        List<DirectoryRedis> directoryRedisList = serviceRedisManagerResources
                .getResourceDirectoryRedisByLoginUser(userLogin);
        directoryRedisList.forEach(directoryRedis -> directoryPaths.add(
                Path.of(directoryRedis.getPathDirectory())));

        directoryPaths.forEach(source -> {
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
    private void deleteDirectories(String userLogin) {
        List<Path> filePaths = new ArrayList<>();

        List<FileRedis> fileRedisList = serviceRedisManagerResources
                .getResourceFileRedisByLoginUser(userLogin);
        fileRedisList.forEach(fileRedis -> filePaths.add(
                Path.of(fileRedis.getPathFile())));

        filePaths.forEach(source -> {
            if (Files.exists(source)) {
                try {
                    serviceDeleteFile.delete(source);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}