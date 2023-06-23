package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.models.user.User;

/**
 * Interface to implement retrieval of all information about the user and its associated objects
 */
public interface ServiceProfileUser {

    /**
     * Get user by login
     * @param login user login
     * @return user model
     */
    User getUserByLogin(String login);

}
