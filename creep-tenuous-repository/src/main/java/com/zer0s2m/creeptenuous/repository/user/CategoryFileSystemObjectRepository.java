package com.zer0s2m.creeptenuous.repository.user;

import com.zer0s2m.creeptenuous.models.user.CategoryFileSystemObject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryFileSystemObjectRepository
        extends CrudRepository<CategoryFileSystemObject, Long> {

    /**
     * Retrieves an object by its ID category, username, and file object system name.
     * @param userCategory_id id category. Must not be {@literal null}.
     * @param user_login user login. Must not be {@literal null}.
     * @param fileSystemObject system name file object. Must not be {@literal null}.
     * @return object
     */
    Optional<CategoryFileSystemObject> findByUserCategory_IdAndUserLoginAndFileSystemObject(
            Long userCategory_id, String user_login, UUID fileSystemObject);

    /**
     * Retrieves an objects by its ID category and username
     * @param userCategory_id id category. Must not be {@literal null}.
     * @param user_login user login. Must not be {@literal null}.
     * @return objects
     */
    Iterable<CategoryFileSystemObject> findAllByUserCategoryIdAndUserLogin(
            Long userCategory_id, String user_login);

    /**
     * Removes all instances with the given file object system names.
     * @param fileSystemObject must not be {@literal null}. Must not contain {@literal null} elements.
     */
    void deleteAllByFileSystemObjectIn(Collection<UUID> fileSystemObject);

}
