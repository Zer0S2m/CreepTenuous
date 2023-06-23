package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.models.user.User;

import java.util.List;

/**
 * Interface for implementing control over users in the system.
 * <ul>
 *     <li>Getting all users on the system</li>
 *     <li>Removing a user from the system</li>
 * </ul>
 */
public interface ServiceControlUser {

    /**
     * Getting all users on the system
     * @return users
     */
    List<User> getAllUsers();

    /**
     * Removing a user from the system by his login
     * @param login user login
     * @throws UserNotFoundException user does not exist in the system
     */
    void deleteUser(String login) throws UserNotFoundException;

}
