package com.zer0s2m.creeptenuous.redis.models.base;

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

}
