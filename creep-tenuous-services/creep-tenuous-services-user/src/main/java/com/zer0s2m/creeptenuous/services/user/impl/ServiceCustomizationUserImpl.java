package com.zer0s2m.creeptenuous.services.user.impl;

import com.zer0s2m.creeptenuous.repository.user.UserColorCategoryRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.user.ServiceCustomizationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Basic service for customizing user file objects
 */
@Service("service-customization-user")
public class ServiceCustomizationUserImpl implements ServiceCustomizationUser {

    private final UserRepository userRepository;

    private final UserColorCategoryRepository userColorCategoryRepository;

    @Autowired
    public ServiceCustomizationUserImpl(
            UserRepository userRepository,
            UserColorCategoryRepository userColorCategoryRepository) {
        this.userRepository = userRepository;
        this.userColorCategoryRepository = userColorCategoryRepository;
    }

    /**
     * Set color scheme for user directory
     * @param fileSystemObject file object name
     * @param userLogin user login. Must not be {@literal null}.
     * @param color color is specified in <b>HEX</b> format
     */
    @Override
    public void setColorInDirectory(
            final String fileSystemObject, final String userLogin, String color) {

    }

    /**
     * Delete color scheme for user directory
     * @param fileSystemObject file object name
     * @param userLogin user login. Must not be {@literal null}.
     * @param color color is specified in <b>HEX</b> format
     */
    @Override
    public void deleteColorInDirectory(
            final String fileSystemObject, final String userLogin, String color) {

    }

}
