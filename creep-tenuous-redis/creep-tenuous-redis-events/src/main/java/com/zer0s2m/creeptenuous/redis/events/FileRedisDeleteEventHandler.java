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
 * Redis Delete Directory Object Handler
 */
@Component
class FileRedisDeleteEventHandler implements ApplicationListener<FileRedisDeleteEvent> {

    static private final String SEPARATOR_UNIQUE_KEY = ManagerRights.SEPARATOR_UNIQUE_KEY.get();

    private final JwtRedisRepository jwtRedisRepository;

    private final RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    @Autowired
    FileRedisDeleteEventHandler(JwtRedisRepository jwtRedisRepository,
                                RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository) {
        this.jwtRedisRepository = jwtRedisRepository;
        this.rightUserFileSystemObjectRedisRepository = rightUserFileSystemObjectRedisRepository;
    }

    /**
     * Handle an application event.
     * @param event the event to respond to
     */
    public void onApplicationEvent(@NotNull FileRedisDeleteEvent event) {
        List<String> userLogins = getUserLogins();
        Iterable<RightUserFileSystemObjectRedis> rightsUser = getRightUserFileSystemObjectRedis(
                userLogins, event.getIdFileRedis());
        deleteRightUserFileSystemObjectRedis(rightsUser);
    }

    /**
     * Get all usernames in the system
     * @return user logins
     */
    private @NotNull List<String> getUserLogins() {
        List<String> userLogins = new ArrayList<>();
        jwtRedisRepository.findAll().forEach(entity -> userLogins.add(entity.getLogin()));
        return userLogins;
    }

    /**
     * Get all user rights by user logins and the system name of the file system object
     * @param userLogins user logins
     * @param nameFileSystemObject system name of the file system object
     * @return  user rights
     */
    private @NotNull Iterable<RightUserFileSystemObjectRedis> getRightUserFileSystemObjectRedis(
            @NotNull List<String> userLogins, String nameFileSystemObject) {
        List<String> ids = new ArrayList<>();
        userLogins.forEach(userLogin -> ids.add(nameFileSystemObject + SEPARATOR_UNIQUE_KEY + userLogin));
        return rightUserFileSystemObjectRedisRepository.findAllById(ids);
    }

    /**
     * Remove user rights on a file system object
     * @param rightsUser user rights
     */
    private void deleteRightUserFileSystemObjectRedis(Iterable<RightUserFileSystemObjectRedis> rightsUser) {
        rightUserFileSystemObjectRedisRepository.deleteAll(rightsUser);
    }

}
