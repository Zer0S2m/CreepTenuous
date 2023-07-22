package com.zer0s2m.creeptenuous.services.user.impl;

import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundUserColorDirectoryException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.models.user.UserColorDirectory;
import com.zer0s2m.creeptenuous.repository.user.UserColorDirectoryRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.user.ServiceCustomizationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Basic service for customizing user file objects
 */
@Service("service-customization-user")
public class ServiceCustomizationUserImpl implements ServiceCustomizationUser {

    private final UserRepository userRepository;

    private final UserColorDirectoryRepository userColorCategoryRepository;

    @Autowired
    public ServiceCustomizationUserImpl(
            UserRepository userRepository,
            UserColorDirectoryRepository userColorCategoryRepository) {
        this.userRepository = userRepository;
        this.userColorCategoryRepository = userColorCategoryRepository;
    }

    /**
     * Set color scheme for user directory. Updates the object, otherwise creates it
     * @param fileSystemObject file object name
     * @param userLogin user login. Must not be {@literal null}.
     * @param color color is specified in <b>HEX</b> format
     * @throws UserNotFoundException not found user
     */
    @Override
    public void setColorInDirectory(
            final String fileSystemObject, final String userLogin, String color) throws NotFoundException {
        User user = userRepository.findByLogin(userLogin);
        if (user == null) {
            throw new UserNotFoundException();
        }

        UUID systemNameDirectory = UUID.fromString(fileSystemObject);
        Optional<UserColorDirectory> userColorCategoryOptional = userColorCategoryRepository
                .findByUserLoginAndDirectory(userLogin, systemNameDirectory);

        UserColorDirectory userColorDirectory;
        if (userColorCategoryOptional.isPresent()) {
            userColorDirectory = userColorCategoryOptional.get();
            userColorDirectory.setColor(color);
        } else {
            userColorDirectory = new UserColorDirectory(user, color, systemNameDirectory);
        }
        userColorCategoryRepository.save(userColorDirectory);
    }

    /**
     * Delete color scheme for user directory
     * @param fileSystemObject file object name
     * @param userLogin user login. Must not be {@literal null}
     * @throws NotFoundUserColorDirectoryException color scheme not found for user directory
     */
    @Override
    public void deleteColorInDirectory(final String fileSystemObject, final String userLogin)
            throws NotFoundException {
        Optional<UserColorDirectory> userColorDirectory = userColorCategoryRepository
                .findByUserLoginAndDirectory(userLogin, UUID.fromString(fileSystemObject));

        if (userColorDirectory.isEmpty()) {
            throw new NotFoundUserColorDirectoryException();
        }

        userColorCategoryRepository.delete(userColorDirectory.get());
    }

}
