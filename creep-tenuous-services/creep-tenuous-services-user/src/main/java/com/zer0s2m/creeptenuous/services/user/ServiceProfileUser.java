package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
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

    /**
     * Set setting for user - deleting a user if it is deleted
     * @param login owner user
     * @param isDelete is deleting
     * @throws UserNotFoundException not exists user
     */
    void setIsDeletingFileObjectSettings(String login, boolean isDelete) throws UserNotFoundException;

    /**
     * Set Setting - Transfer File Objects to Designated User
     * @param login owner user
     * @param transferUserId designated user for migration
     * @throws UserNotFoundException not exists user
     */
    void setTransferredUserSettings(String login, Long transferUserId) throws UserNotFoundException;

}
