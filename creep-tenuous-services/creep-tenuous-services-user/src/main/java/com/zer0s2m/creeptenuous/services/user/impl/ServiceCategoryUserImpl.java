package com.zer0s2m.creeptenuous.services.user.impl;

import com.zer0s2m.creeptenuous.common.containers.ContainerCategoryFileSystemObject;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataUserCategory;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundCategoryFileSystemObjectException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundUserCategoryException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.models.user.CategoryFileSystemObject;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.models.user.UserCategory;
import com.zer0s2m.creeptenuous.models.user.UserColorCategory;
import com.zer0s2m.creeptenuous.repository.user.CategoryFileSystemObjectRepository;
import com.zer0s2m.creeptenuous.repository.user.UserCategoryRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.user.ServiceCategoryUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Basic service for working with an entity - a category for a user
 */
@Service("service-category-user")
public class ServiceCategoryUserImpl implements ServiceCategoryUser {

    private final UserCategoryRepository userCategoryRepository;

    private final CategoryFileSystemObjectRepository categoryFileSystemObjectRepository;

    private final UserRepository userRepository;

    @Autowired
    public ServiceCategoryUserImpl(
            UserCategoryRepository userCategoryRepository,
            CategoryFileSystemObjectRepository categoryFileSystemObjectRepository,
            UserRepository userRepository) {
        this.userCategoryRepository = userCategoryRepository;
        this.categoryFileSystemObjectRepository = categoryFileSystemObjectRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all custom categories
     *
     * @param userLogin user login
     * @return entities
     */
    @Override
    public List<ContainerDataUserCategory> getAll(final String userLogin) {
        final List<ContainerDataUserCategory> userCategoryList = new ArrayList<>();
        userCategoryRepository.findAllByUserLogin(userLogin)
                .forEach(userCategory -> {
                    UserColorCategory userColorCategory = userCategory.getUserColorCategory();
                    userCategoryList.add(new ContainerDataUserCategory(
                            userCategory.getId(),
                            userCategory.getTitle(),
                            userColorCategory != null ? userColorCategory.getUserColor().getColor() : null,
                            userColorCategory != null ? userColorCategory.getUserColor().getId() : null
                    ));
                });
        return userCategoryList;
    }

    /**
     * Creating a category for file objects
     *
     * @param title     Category name
     * @param userLogin login user
     * @return entity data
     * @throws UserNotFoundException user not exists
     */
    @Override
    public ContainerDataUserCategory create(final String title, final String userLogin)
            throws UserNotFoundException {
        final User user = getUserByLoginUser(userLogin);
        UserCategory userCategory = userCategoryRepository.save(new UserCategory(title, user));
        return new ContainerDataUserCategory(userCategory.getId(), userCategory.getTitle(), null, null);
    }

    /**
     * Update custom category by id
     *
     * @param id        ID entity
     * @param title     new title user category
     * @param userLogin user login
     * @throws NotFoundException not found user category
     */
    @Override
    public void edit(final Long id, final String title, final String userLogin) throws NotFoundException {
        Optional<UserCategory> userCategoryOptional = userCategoryRepository.findByIdAndUser_Login(id, userLogin);
        if (userCategoryOptional.isEmpty()) {
            throw new NotFoundUserCategoryException();
        }

        UserCategory userCategory = userCategoryOptional.get();
        userCategory.setTitle(title);
        userCategoryRepository.save(userCategory);
    }

    /**
     * Delete comment file system object
     *
     * @param id        id comment. Must not be {@literal null}.
     * @param userLogin user login. Must not be {@literal null}.
     * @throws NotFoundException not found user category
     */
    @Override
    public void delete(final Long id, final String userLogin) throws NotFoundException {
        if (!userCategoryRepository.existsByIdAndUserLogin(id, userLogin)) {
            throw new NotFoundUserCategoryException();
        }
        userCategoryRepository.deleteById(id);
    }

    /**
     * Find a user by his login
     *
     * @param loginUser login user
     * @return user
     * @throws UserNotFoundException user not exists
     */
    private @NotNull User getUserByLoginUser(final String loginUser) throws UserNotFoundException {
        User user = userRepository.findByLogin(loginUser);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    /**
     * Bind a file object to a custom category
     *
     * @param categoryId       id category. Must not be {@literal null}.
     * @param fileSystemObject file object name
     * @param userLogin        user login. Must not be {@literal null}.
     * @throws NotFoundException not found category or user
     */
    @Override
    @Transactional
    public void setFileSystemObjectInCategory(
            final Long categoryId, final String fileSystemObject, final String userLogin)
            throws NotFoundException {
        User user = userRepository.findByLogin(userLogin);
        if (user == null) {
            throw new UserNotFoundException();
        }

        Optional<UserCategory> userCategoryOptional = userCategoryRepository.findByIdAndUser_Login(
                categoryId, userLogin);
        if (userCategoryOptional.isEmpty()) {
            throw new NotFoundUserCategoryException();
        }

        CategoryFileSystemObject categoryFileSystemObject = new CategoryFileSystemObject(
                user, userCategoryOptional.get(), UUID.fromString(fileSystemObject));

        categoryFileSystemObjectRepository.deleteAllByFileSystemObjectIn(
                List.of(UUID.fromString(fileSystemObject)));

        categoryFileSystemObjectRepository.save(categoryFileSystemObject);
    }

    /**
     * Link a file object to a custom category
     *
     * @param fileSystemObject file object name
     * @param userLogin        user login. Must not be {@literal null}.
     * @throws NotFoundException not found category or linked file category objects to user category
     */
    @Override
    @Transactional
    public void unsetFileSystemObjectInCategory(final String fileSystemObject, final String userLogin)
            throws NotFoundException {
        long categoryFileSystemObject = categoryFileSystemObjectRepository
                .deleteAllByFileSystemObjectIn(List.of(UUID.fromString(fileSystemObject)));
        if (categoryFileSystemObject == 0) {
            throw new NotFoundCategoryFileSystemObjectException();
        }
    }

    /**
     * Get all objects of the file category associated with the user category by ID
     *
     * @param categoryId id category. Must not be {@literal null}.
     * @param userLogin  user login. Must not be {@literal null}.
     * @return linked file category objects to user category
     * @throws NotFoundException not found category
     */
    @Override
    public List<ContainerCategoryFileSystemObject> getFileSystemObjectInCategoryByCategoryId(
            final Long categoryId, final String userLogin) throws NotFoundException {
        if (!userCategoryRepository.existsByIdAndUserLogin(categoryId, userLogin)) {
            throw new NotFoundUserCategoryException();
        }

        List<ContainerCategoryFileSystemObject> categoryFileSystemObjects = new ArrayList<>();

        categoryFileSystemObjectRepository.findAllByUserCategoryIdAndUserLogin(
                categoryId, userLogin
        ).forEach(categoryFileSystemObject -> categoryFileSystemObjects.add(new ContainerCategoryFileSystemObject(
                categoryId, categoryFileSystemObject.getFileSystemObject().toString())));

        return categoryFileSystemObjects;
    }

}
