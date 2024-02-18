package com.zer0s2m.creeptenuous.services.user.impl;

import com.zer0s2m.creeptenuous.common.containers.ContainerCustomColorApi;
import com.zer0s2m.creeptenuous.common.exceptions.*;
import com.zer0s2m.creeptenuous.models.user.*;
import com.zer0s2m.creeptenuous.repository.user.*;
import com.zer0s2m.creeptenuous.services.user.ServiceCustomizationUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Basic service for customizing user file objects
 */
@Service("service-customization-user")
public class ServiceCustomizationUserImpl implements ServiceCustomizationUser {

    private final UserRepository userRepository;

    private final UserColorDirectoryRepository userColorDirectoryRepository;

    private final UserColorRepository userColorRepository;

    private final UserCategoryRepository userCategoryRepository;
    
    private final UserColorCategoryRepository userColorCategoryRepository;

    @Autowired
    public ServiceCustomizationUserImpl(
            UserRepository userRepository,
            UserColorDirectoryRepository userColorDirectoryRepository,
            UserColorRepository userColorRepository,
            UserCategoryRepository userCategoryRepository,
            UserColorCategoryRepository userColorCategoryRepository) {
        this.userRepository = userRepository;
        this.userColorDirectoryRepository = userColorDirectoryRepository;
        this.userColorRepository = userColorRepository;
        this.userCategoryRepository = userCategoryRepository;
        this.userColorCategoryRepository = userColorCategoryRepository;
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
        Optional<UserColorDirectory> userColorCategoryOptional = userColorDirectoryRepository
                .findByUserLoginAndDirectory(userLogin, systemNameDirectory);

        UserColorDirectory userColorDirectory;
        if (userColorCategoryOptional.isPresent()) {
            userColorDirectory = userColorCategoryOptional.get();
            userColorDirectory.setColor(getUserColorByIdAndUserLogin(userColorId, userLogin));
        } else {
            userColorDirectory = new UserColorDirectory(user,
                    getUserColorByIdAndUserLogin(userColorId, userLogin), systemNameDirectory);
        }
        userColorDirectoryRepository.save(userColorDirectory);
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
        Optional<UserColorDirectory> userColorDirectory = userColorDirectoryRepository
                .findByUserLoginAndDirectory(userLogin, UUID.fromString(fileSystemObject));

        if (userColorDirectory.isEmpty()) {
            throw new NotFoundUserColorDirectoryException();
        }

        userColorDirectoryRepository.delete(userColorDirectory.get());
    }

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
    @Override
    @Transactional
    public void setColorInCategory(final String userLogin, final Long userColorId, final Long userCategoryId)
            throws NotFoundException {
        userColorCategoryRepository
                .deleteByUserCategoryIdAndUserLogin(userCategoryId, userLogin);

        userColorCategoryRepository.save(new UserColorCategory(
                getUserByLogin(userLogin),
                getUserColorByIdAndUserLogin(userColorId, userLogin),
                getUserCategoryByUserLongAndId(userLogin, userCategoryId)
        ));
    }

    /**
     * Get all custom colors
     * @param userLogin user login. Must not be {@literal null}
     * @return entities user colors
     */
    @Override
    public List<ContainerCustomColorApi> getColors(final String userLogin) {
        List<ContainerCustomColorApi> colorApiList = new ArrayList<>();
        userColorRepository.findAllByUserLogin(userLogin)
                .forEach((obj) ->
                        colorApiList.add(new ContainerCustomColorApi(obj.getColor(), obj.getId())));
        return colorApiList;
    }

    /**
     * Delete color scheme binding to custom category
     *
     * @param userLogin      user login. Must not be {@literal null}
     * @param userCategoryId ID entity {@link com.zer0s2m.creeptenuous.models.user.UserCategory}.
     *                       Must not be {@literal null}
     * @throws NotFoundUserColorCategoryException custom category color scheme binding not found
     */
    @Override
    @Transactional
    public void deleteColorInCategory(final String userLogin, final Long userCategoryId)
            throws NotFoundException {
        long countDeletedObjects = userColorCategoryRepository.deleteByUserCategoryIdAndUserLogin(
                userCategoryId, userLogin);
        if (countDeletedObjects == 0) {
            throw new NotFoundUserColorCategoryException();
        }
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
     * @throws UserNotFoundException not found user
     */
    private @NotNull User getUserByLogin(final String userLogin) throws NotFoundException {
        User user = userRepository.findByLogin(userLogin);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    /**
     * Get user category
     * @param userLogin user login. Must not be {@literal null}
     * @param id ID entity Must not be {@literal null}
     * @return entity
     * @throws NotFoundUserCategoryException not found the user category
     */
    private @NotNull UserCategory getUserCategoryByUserLongAndId(final String userLogin, final Long id)
            throws NotFoundException {
        Optional<UserCategory> userCategoryRaw = userCategoryRepository.findByIdAndUser_Login(
                id, userLogin);
        if (userCategoryRaw.isEmpty()) {
            throw new NotFoundUserCategoryException();
        }
        return userCategoryRaw.get();
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
