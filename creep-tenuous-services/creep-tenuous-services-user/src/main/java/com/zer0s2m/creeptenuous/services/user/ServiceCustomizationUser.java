package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.containers.ContainerCustomColorApi;
import com.zer0s2m.creeptenuous.common.exceptions.*;

import java.util.List;

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
     * Set color scheme binding to custom category
     *
     * @param userLogin      user login. Must not be {@literal null}
     * @param userColorId    ID entity {@link com.zer0s2m.creeptenuous.models.user.UserColor}.
     *                       Must not be {@literal null}
     * @param userCategoryId ID entity {@link com.zer0s2m.creeptenuous.models.user.UserCategory}.
     *                       Must not be {@literal null}
     * @throws NotFoundUserCategoryException not found the user category
     * @throws NotFoundUserColorException    not found user color entity
     * @throws UserNotFoundException         not found user color
     */
    void setColorInCategory(final String userLogin, final Long userColorId, final Long userCategoryId)
            throws NotFoundException;

    /**
     * Delete color scheme binding to custom category
     *
     * @param userLogin      user login. Must not be {@literal null}
     * @param userCategoryId ID entity {@link com.zer0s2m.creeptenuous.models.user.UserCategory}.
     *                       Must not be {@literal null}
     * @throws NotFoundUserColorCategoryException custom category color scheme binding not found
     */
    void deleteColorInCategory(final String userLogin, final Long userCategoryId)
            throws NotFoundException;

    /**
     * Get all custom colors
     * @param userLogin user login. Must not be {@literal null}
     * @return entities user colors
     */
    List<ContainerCustomColorApi> getColors(final String userLogin);

    /**
     * Create custom color
     * @param userLogin user login. Must not be {@literal null}
     * @param color color is specified in <b>HEX</b> format
     * @throws UserNotFoundException not found user
     */
    void createColor(final String userLogin, String color) throws NotFoundException;

    /**
     * Edit custom color
     * @param userLogin user login. Must not be {@literal null}
     * @param id id entity. Must not be {@literal null}
     * @param newColor new color is specified in <b>HEX</b> format
     * @throws NotFoundUserColorException not found user color
     */
    void editColor(final String userLogin, final Long id, String newColor) throws NotFoundException;

    /**
     * Delete custom color
     * @param userLogin user login. Must not be {@literal null}
     * @param id id entity. Must not be {@literal null}
     * @throws NotFoundUserColorException not found user color
     */
    void deleteColor(final String userLogin, final Long id) throws NotFoundException;

}
