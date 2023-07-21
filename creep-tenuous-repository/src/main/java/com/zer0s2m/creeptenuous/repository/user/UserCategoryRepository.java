package com.zer0s2m.creeptenuous.repository.user;

import com.zer0s2m.creeptenuous.models.user.UserCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCategoryRepository extends CrudRepository<UserCategory, Long> {

    /**
     * Returns whether an entity exists with the given id and username.
     * @param id id category. Must not be {@literal null}.
     * @param user_login user login. Must not be {@literal null}.
     * @return is exists
     */
    boolean existsByIdAndUserLogin(Long id, String user_login);

    /**
     * Retrieves an object by its ID and username.
     * @param id id entity. Must not be {@literal null}.
     * @param user_login user login. Must not be {@literal null}.
     * @return category
     */
    Optional<UserCategory> findByIdAndUser_Login(Long id, String user_login);

    /**
     * Retrieves objects by username
     * @param user_login user login. Must not be {@literal null}.
     * @return categories
     */
    Iterable<UserCategory> findAllByUserLogin(String user_login);

}
