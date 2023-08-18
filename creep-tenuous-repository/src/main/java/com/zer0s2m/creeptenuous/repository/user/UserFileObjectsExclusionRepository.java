package com.zer0s2m.creeptenuous.repository.user;

import com.zer0s2m.creeptenuous.models.user.UserFileObjectsExclusion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface UserFileObjectsExclusionRepository extends CrudRepository<UserFileObjectsExclusion, Long> {

    /**
     * Delete all entities by system names of file objects and user login
     * @param fileSystemObject system names of file objects.
     *                         Must not be {@literal null} nor must it contain {@literal null}.
     * @param user_login user login. Must not be {@literal null}.
     * @return deleted count entities
     */
    long deleteAllByFileSystemObjectInAndUserLogin(Collection<UUID> fileSystemObject, String user_login);

}
