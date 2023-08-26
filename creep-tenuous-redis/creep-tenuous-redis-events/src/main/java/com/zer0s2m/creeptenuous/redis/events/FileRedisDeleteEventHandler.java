package com.zer0s2m.creeptenuous.redis.events;

import com.zer0s2m.creeptenuous.common.enums.ManagerRights;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repository.JwtRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.RightUserFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.repository.user.CategoryFileSystemObjectRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Redis Delete File Object Handler
 */
@Component
class FileRedisDeleteEventHandler implements ApplicationListener<FileRedisDeleteEvent> {

    static private final String SEPARATOR_UNIQUE_KEY = ManagerRights.SEPARATOR_UNIQUE_KEY.get();

    private final JwtRedisRepository jwtRedisRepository;

    private final RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    private final CategoryFileSystemObjectRepository categoryFileSystemObjectRepository;

    @Autowired
    FileRedisDeleteEventHandler(JwtRedisRepository jwtRedisRepository,
                                RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository,
                                CategoryFileSystemObjectRepository categoryFileSystemObjectRepository) {
        this.jwtRedisRepository = jwtRedisRepository;
        this.rightUserFileSystemObjectRedisRepository = rightUserFileSystemObjectRedisRepository;
        this.categoryFileSystemObjectRepository = categoryFileSystemObjectRepository;
    }

    /**
     * Handle an application event.
     * @param event the event to respond to
     */
    public void onApplicationEvent(@NotNull FileRedisDeleteEvent event) {
        List<String> userLogins = UtilsEventHandler.getUserLogins(jwtRedisRepository);
        Iterable<RightUserFileSystemObjectRedis> rightsUser = getRightUserFileSystemObjectRedis(
                userLogins, event.getIdFileRedis());
        deleteRightUserFileSystemObjectRedis(rightsUser);
        deleteFileObjectCustomCategories(event.getIdFileRedis());
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

    /**
     * Deleting file objects associated witt custom categories
     * @param nameFileSystemObject system name of the file system object
     */
    private void deleteFileObjectCustomCategories(final @NotNull String nameFileSystemObject) {
        if (nameFileSystemObject.length() == 36) {
            categoryFileSystemObjectRepository.deleteAllByFileSystemObjectIn(
                    List.of(UUID.fromString(nameFileSystemObject)));
        }
    }

}
