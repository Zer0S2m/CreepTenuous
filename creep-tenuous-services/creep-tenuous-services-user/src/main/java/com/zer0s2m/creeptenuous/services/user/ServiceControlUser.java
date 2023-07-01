package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.models.user.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface for implementing control over users in the system.
 * <ul>
 *     <li>Getting all users on the system</li>
 *     <li>Removing a user from the system</li>
 *     <li>Account blocking</li>
 *     <li>Blocking a user for a while</li>
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

    /**
     * Block a user in the system by his login
     * @param login user login
     * @throws UserNotFoundException user does not exist in the system
     */
    void blockUser(String login) throws UserNotFoundException;

    /**
     * Blocking a user by his login for a while
     * @param login user login
     * @param fromDate block start date
     * @param toDate lock end date
     * @throws UserNotFoundException user does not exist in the system
     */
    void blockUserTemporarily(String login, LocalDateTime fromDate, LocalDateTime toDate)
            throws UserNotFoundException;

    /**
     * Unblock a user in the system by his login
     * @param login user login
     * @throws UserNotFoundException user does not exist in the system
     */
    void unblockUser(String login) throws UserNotFoundException;

}
