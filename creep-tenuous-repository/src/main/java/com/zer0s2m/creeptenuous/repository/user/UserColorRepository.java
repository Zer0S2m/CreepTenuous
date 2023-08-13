package com.zer0s2m.creeptenuous.repository.user;

import com.zer0s2m.creeptenuous.models.user.UserColor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserColorRepository extends CrudRepository<UserColor, Long> {

    /**
     * Retrieves an object by its id and username.
     * @param id id entity. Must not be {@literal null}.
     * @param user_login user login. Must not be {@literal null}.
     * @return object
     */
    Optional<UserColor> findByIdAndUserLogin(Long id, String user_login);

    Iterable<UserColor> findAllByUserLogin(String user_login);

}
