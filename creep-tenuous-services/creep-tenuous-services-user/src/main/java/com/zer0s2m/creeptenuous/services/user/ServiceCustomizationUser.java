package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundUserColorDirectoryException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;

/**
 * Basic interface for implementing customization of user file objects
 */
public interface ServiceCustomizationUser {

    /**
     * Set color scheme for user directory. Updates the object, otherwise creates it
     * @param fileSystemObject file object name
     * @param userLogin user login. Must not be {@literal null}.
     * @param color color is specified in <b>HEX</b> format
     * @throws UserNotFoundException not found user
     */
    void setColorInDirectory(
            final String fileSystemObject, final String userLogin, String color) throws NotFoundException;

    /**
     * Delete color scheme for user directory
     * @param fileSystemObject file object name
     * @param userLogin user login. Must not be {@literal null}
     * @throws NotFoundUserColorDirectoryException color scheme not found for user directory
     */
    void deleteColorInDirectory(final String fileSystemObject, final String userLogin) throws NotFoundException;

}
