package com.zer0s2m.creeptenuous.redis.events;

import com.zer0s2m.creeptenuous.common.enums.ManagerRights;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repository.JwtRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.RightUserFileSystemObjectRedisRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Redis Delete File Object Handler
 */
@Component
class DirectoryRedisDeleteEventHandler implements ApplicationListener<DirectoryRedisDeleteEvent> {

    static private final String SEPARATOR_UNIQUE_KEY = ManagerRights.SEPARATOR_UNIQUE_KEY.get();

    private final JwtRedisRepository jwtRedisRepository;

    private final RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    private final ServiceDirectoryRedisDeleteEventHandler serviceDirectoryRedisDeleteEventHandler;

    @Autowired
    DirectoryRedisDeleteEventHandler(JwtRedisRepository jwtRedisRepository,
                                     RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository,
                                     ServiceDirectoryRedisDeleteEventHandler serviceDirectoryRedisDeleteEventHandler) {
        this.jwtRedisRepository = jwtRedisRepository;
        this.rightUserFileSystemObjectRedisRepository = rightUserFileSystemObjectRedisRepository;
        this.serviceDirectoryRedisDeleteEventHandler = serviceDirectoryRedisDeleteEventHandler;
    }

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    public void onApplicationEvent(@NotNull DirectoryRedisDeleteEvent event) {
        List<String> userLogins = UtilsEventHandler.getUserLogins(jwtRedisRepository);
        Iterable<RightUserFileSystemObjectRedis> rightsUser = getRightUserFileSystemObjectRedis(
                userLogins, event.getNamesFileSystemObject());
        deleteRightUserFileSystemObjectRedis(rightsUser);
        serviceDirectoryRedisDeleteEventHandler.deleteFileObjectCustomCategories(event.getNamesFileSystemObject());
    }

    /**
     * Get all user rights by user logins and the system name of the file system object
     *
     * @param userLogins            user logins
     * @param namesFileSystemObject system names of the file system object
     * @return user rights
     */
    private @NotNull Iterable<RightUserFileSystemObjectRedis> getRightUserFileSystemObjectRedis(
            @NotNull List<String> userLogins, List<String> namesFileSystemObject) {
        List<String> ids = new ArrayList<>();
        userLogins.forEach(userLogin -> namesFileSystemObject.forEach(nameFileSystemObject ->
                ids.add(nameFileSystemObject + SEPARATOR_UNIQUE_KEY + userLogin)));
        return rightUserFileSystemObjectRedisRepository.findAllById(ids);
    }

    /**
     * Remove user rights on a file system object
     *
     * @param rightsUser user rights
     */
    private void deleteRightUserFileSystemObjectRedis(Iterable<RightUserFileSystemObjectRedis> rightsUser) {
        rightUserFileSystemObjectRedisRepository.deleteAll(rightsUser);
    }

}
