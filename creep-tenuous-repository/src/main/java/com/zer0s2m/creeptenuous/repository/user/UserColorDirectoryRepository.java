package com.zer0s2m.creeptenuous.repository.user;

import com.zer0s2m.creeptenuous.models.user.UserColorDirectory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserColorDirectoryRepository extends CrudRepository<UserColorDirectory, Long> {

    /**
     * Retrieves an object by its username and file object system name.
     * @param user_login user login. Must not be {@literal null}.
     * @param directory system name file object. Must not be {@literal null}.
     * @return object
     */
    Optional<UserColorDirectory> findByUserLoginAndDirectory(String user_login, UUID directory);

    /**
     * Retrieves objects by secondary field - file system object type
     * @param directory names directories. Must not be {@literal null}.
     * @return objects
     */
    Iterable<UserColorDirectory> findAllByDirectoryIn(Collection<UUID> directory);

}
