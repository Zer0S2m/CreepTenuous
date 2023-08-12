package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundUserColorDirectoryException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundUserColorException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;

/**
 * Basic interface for implementing customization of user file objects
 */
public interface ServiceCustomizationUser {

    /**
     * Set color scheme for user directory. Updates the object, otherwise creates it
     * @param fileSystemObject file object name
     * @param userLogin user login. Must not be {@literal null}.
     * @param userColorId color is specified in <b>HEX</b> format
     * @throws UserNotFoundException not found user
     * @throws NotFoundUserColorException not found user color
     */
    void setColorInDirectory(
            final String fileSystemObject, final String userLogin, Long userColorId) throws NotFoundException;

    /**
     * Delete color scheme for user directory
     * @param fileSystemObject file object name
     * @param userLogin user login. Must not be {@literal null}
     * @throws NotFoundUserColorDirectoryException color scheme not found for user directory
     */
    void deleteColorInDirectory(final String fileSystemObject, final String userLogin) throws NotFoundException;

    /**
     * Create custom color
     * @param userLogin user login. Must not be {@literal null}
     * @param color color is specified in <b>HEX</b> format
     */
    void createColor(final String userLogin, String color);

    /**
     * Edit custom color
     * @param userLogin user login. Must not be {@literal null}
     * @param id id entity. Must not be {@literal null}
     * @param newColor new color is specified in <b>HEX</b> format
     */
    void editColor(final String userLogin, final Long id, String newColor) throws NotFoundException;

    /**
     * Delete custom color
     * @param userLogin user login. Must not be {@literal null}
     * @param id id entity. Must not be {@literal null}
     */
    void deleteColor(final String userLogin, final Long id) throws NotFoundException;

}
