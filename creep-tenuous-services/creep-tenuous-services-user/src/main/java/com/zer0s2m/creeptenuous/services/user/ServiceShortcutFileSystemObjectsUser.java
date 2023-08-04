package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;

import java.util.UUID;

/**
 * Basic interface for building interaction with file object labels
 */
public interface ServiceShortcutFileSystemObjectsUser {

    /**
     * Create a shortcut to a file object
     * @param userLogin user login
     * @param attachedFileSystemObject attached file object
     * @param toAttachedFileSystemObject file object to be attached to
     * @throws UserNotFoundException user not exists
     */
    void create(String userLogin, UUID attachedFileSystemObject, UUID toAttachedFileSystemObject)
            throws UserNotFoundException;

    /**
     * Delete a shortcut to a file object
     * @param userLogin user login
     * @param attachedFileSystemObject attached file object
     * @param toAttachedFileSystemObject file object to be attached to
     * @throws UserNotFoundException user not exists
     */
    void delete(String userLogin, UUID attachedFileSystemObject, UUID toAttachedFileSystemObject)
            throws UserNotFoundException;

}
