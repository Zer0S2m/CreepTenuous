package com.zer0s2m.creeptenuous.redis.models.base;

import java.util.List;

/**
 * Base class for Redis hashes of filesystem objects and permissions
 */
public interface BaseRedis {

    /**
     * Get login user
     * @return login user
     */
    String getLogin();

    /**
     * Set login user
     * @param login login user
     */
    void setLogin(String login);

    /**
     * Get logins of users who have access to the object.
     * @return user logins.
     */
    List<String> getUserLogins();

    /**
     * Set logins of users who have access to the object
     * @param userLogins user logins.
     */
    void setUserLogins(List<String> userLogins);

}
