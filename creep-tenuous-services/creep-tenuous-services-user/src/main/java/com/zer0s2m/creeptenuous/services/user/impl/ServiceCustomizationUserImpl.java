package com.zer0s2m.creeptenuous.services.user.impl;

import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundUserColorDirectoryException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundUserColorException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.models.user.UserColor;
import com.zer0s2m.creeptenuous.models.user.UserColorDirectory;
import com.zer0s2m.creeptenuous.repository.user.UserColorDirectoryRepository;
import com.zer0s2m.creeptenuous.repository.user.UserColorRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.user.ServiceCustomizationUser;
import org.jetbrains.annotations.NotNull;
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

    private final UserColorRepository userColorRepository;

    @Autowired
    public ServiceCustomizationUserImpl(
            UserRepository userRepository,
            UserColorDirectoryRepository userColorCategoryRepository,
            UserColorRepository userColorRepository) {
        this.userRepository = userRepository;
        this.userColorCategoryRepository = userColorCategoryRepository;
        this.userColorRepository = userColorRepository;
    }

    /**
     * Set color scheme for user directory. Updates the object, otherwise creates it
     * @param fileSystemObject file object name
     * @param userLogin user login. Must not be {@literal null}.
     * @param userColorId ID from entity {@link UserColor}. Must not be {@literal null}.
     * @throws UserNotFoundException not found user
     * @throws NotFoundUserColorException not found user color
     */
    @Override
    public void setColorInDirectory(
            final String fileSystemObject, final String userLogin, Long userColorId) throws NotFoundException {
        User user = getUserByLogin(userLogin);

        UUID systemNameDirectory = UUID.fromString(fileSystemObject);
        Optional<UserColorDirectory> userColorCategoryOptional = userColorCategoryRepository
                .findByUserLoginAndDirectory(userLogin, systemNameDirectory);

        UserColorDirectory userColorDirectory;
        if (userColorCategoryOptional.isPresent()) {
            userColorDirectory = userColorCategoryOptional.get();
            userColorDirectory.setColor(getUserColorByIdAndUserLogin(userColorId, userLogin));
        } else {
            userColorDirectory = new UserColorDirectory(user,
                    getUserColorByIdAndUserLogin(userColorId, userLogin), systemNameDirectory);
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

    /**
     * Get entity user color
     * @param id ID from entity {@link UserColor}. Must not be {@literal null}.
     * @param userLogin user login. Must not be {@literal null}
     * @return entity
     * @throws NotFoundUserColorException not found user color
     */
    private @NotNull UserColor getUserColorByIdAndUserLogin(final Long id, final String userLogin)
            throws NotFoundException {
        Optional<UserColor> userColorRaw = userColorRepository.findByIdAndUserLogin(id, userLogin);
        if (userColorRaw.isEmpty()) {
            throw new NotFoundUserColorException();
        }
        return userColorRaw.get();
    }

    /**
     * Get user bu login
     * @param userLogin user login. Must not be {@literal null}
     * @return entity
     * @throws NotFoundException not found user
     */
    private @NotNull User getUserByLogin(final String userLogin) throws NotFoundException {
        User user = userRepository.findByLogin(userLogin);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    /**
     * Create custom color
     * @param userLogin user login. Must not be {@literal null}
     * @param color color is specified in <b>HEX</b> format
     * @throws UserNotFoundException not found user
     */
    @Override
    public void createColor(final String userLogin, String color) throws NotFoundException {
        userColorRepository.save(new UserColor(getUserByLogin(userLogin), color));
    }

    /**
     * Edit custom color
     * @param userLogin user login. Must not be {@literal null}
     * @param id id entity. Must not be {@literal null}
     * @param newColor new color is specified in <b>HEX</b> format
     * @throws NotFoundUserColorException not found user color
     */
    @Override
    public void editColor(final String userLogin, final Long id, String newColor) throws NotFoundException {
        UserColor userColor = getUserColorByIdAndUserLogin(id, userLogin);
        userColor.setColor(newColor);
        userColorRepository.save(userColor);
    }

    /**
     * Delete custom color
     * @param userLogin user login. Must not be {@literal null}
     * @param id id entity. Must not be {@literal null}
     * @throws NotFoundUserColorException not found user color
     */
    @Override
    public void deleteColor(final String userLogin, final Long id) throws NotFoundException {
        userColorRepository.delete(getUserColorByIdAndUserLogin(id, userLogin));
    }

}
