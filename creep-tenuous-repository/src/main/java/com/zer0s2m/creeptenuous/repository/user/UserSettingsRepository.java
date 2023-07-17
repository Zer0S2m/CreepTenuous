package com.zer0s2m.creeptenuous.repository.user;

import com.zer0s2m.creeptenuous.models.user.UserSettings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingsRepository extends CrudRepository<UserSettings, Long> {

    /**
     * Retrieves an object by username user login
     * @param user_login user login. Must not be {@literal null}.
     * @return result
     */
    Optional<UserSettings> findByUser_Login(String user_login);

}
