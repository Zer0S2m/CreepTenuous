package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.models.user.User;

import java.util.List;

/**
 * Interface for implementing control over users in the system.
 * <ul>
 *     <li>Getting all users on the system</li>
 * </ul>
 */
public interface ServiceControlUser {

    /**
     * Getting all users on the system
     * @return users
     */
    List<User> getAllUsers();

}
