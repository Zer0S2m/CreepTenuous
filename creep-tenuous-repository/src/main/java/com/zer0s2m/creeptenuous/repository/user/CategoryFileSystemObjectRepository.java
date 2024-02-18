package com.zer0s2m.creeptenuous.repository.user;

import com.zer0s2m.creeptenuous.common.query.IMapFileObjectToCategory;
import com.zer0s2m.creeptenuous.models.user.CategoryFileSystemObject;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
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
    long deleteAllByFileSystemObjectIn(Collection<UUID> fileSystemObject);

    /**
     * Get all file objects that are associated with any user categories by user login.
     * @param userLogin user login. Must not be {@literal null}.
     * @return entities.
     */
    @Query(
            value = "SELECT cfso.user_category_id as userCategoryId, ARRAY_AGG(cfso.file_system_object) AS fileSystemObjects " +
                    "FROM category_file_system_objects AS cfso " +
                    "         LEFT JOIN user_categories uc ON cfso.user_category_id = uc.id " +
                    "         LEFT JOIN \"user\" u ON u.id = cfso.user_id " +
                    "WHERE u.login = ?1 " +
                    "GROUP BY userCategoryId",
            nativeQuery = true
    )
    List<IMapFileObjectToCategory> getMapFileObjectSettingToCategory(final String userLogin);

}
