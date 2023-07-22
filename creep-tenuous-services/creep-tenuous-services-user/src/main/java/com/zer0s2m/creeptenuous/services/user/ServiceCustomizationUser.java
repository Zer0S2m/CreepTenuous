package com.zer0s2m.creeptenuous.services.user;

/**
 * Basic interface for implementing customization of user file objects
 */
public interface ServiceCustomizationUser {

    /**
     * Set color scheme for user directory
     * @param fileSystemObject file object name
     * @param userLogin user login. Must not be {@literal null}.
     * @param color color is specified in <b>HEX</b> format
     */
    void setColorInDirectory(
            final String fileSystemObject, final String userLogin, String color);

    /**
     * Delete color scheme for user directory
     * @param fileSystemObject file object name
     * @param userLogin user login. Must not be {@literal null}.
     * @param color color is specified in <b>HEX</b> format
     */
    void deleteColorInDirectory(
            final String fileSystemObject, final String userLogin, String color);

}
