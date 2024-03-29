package com.zer0s2m.creeptenuous.security.services;

/**
 * Service for creating and verifying passwords for users in the system
 */
public interface GeneratePassword {

    /**
     * Generate password
     * @param password raw password
     * @return hash password
     */
    String generation(String password);

    /**
     * Verify password
     * @param password raw password
     * @param hashPassword hash password
     * @return is valid
     */
    Boolean verify(String password, String hashPassword);

}
