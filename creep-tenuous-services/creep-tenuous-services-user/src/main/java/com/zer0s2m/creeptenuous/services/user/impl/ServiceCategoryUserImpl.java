package com.zer0s2m.creeptenuous.services.user.impl;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataUserCategory;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundUserCategoryException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.models.user.UserCategory;
import com.zer0s2m.creeptenuous.repository.user.UserCategoryRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.user.ServiceCategoryUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Basic service for working with an entity - a category for a user
 */
@Service("service-category-user")
public class ServiceCategoryUserImpl implements ServiceCategoryUser {

    private final UserCategoryRepository userCategoryRepository;

    private final UserRepository userRepository;

    @Autowired
    public ServiceCategoryUserImpl(UserCategoryRepository userCategoryRepository, UserRepository userRepository) {
        this.userCategoryRepository = userCategoryRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all custom categories
     * @param userLogin user login
     * @return entities
     */
    @Override
    public List<ContainerDataUserCategory> getAll(final String userLogin) {
        final List<ContainerDataUserCategory> userCategoryList = new ArrayList<>();
        userCategoryRepository.findAllByUserLogin(userLogin)
                .forEach(userCategory -> userCategoryList.add(new ContainerDataUserCategory(
                        userCategory.getId(), userCategory.getTitle())));
        return userCategoryList;
    }

    /**
     * Creating a category for file objects
     * @param title Category name
     * @param userLogin login user
     * @return entity data
     * @throws UserNotFoundException user not exists
     */
    @Override
    public ContainerDataUserCategory create(final String title, final String userLogin)
            throws UserNotFoundException {
        final User user = getUserByLoginUser(userLogin);
        UserCategory userCategory = userCategoryRepository.save(new UserCategory(title, user));
        return new ContainerDataUserCategory(userCategory.getId(), userCategory.getTitle());
    }

    /**
     * Update custom category by id
     * @param id ID entity
     * @param title new title user category
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
     * @param id id comment. Must not be {@literal null}.
     * @param userLogin user login. Must not be {@literal null}.
     * @throws NotFoundException not found user category
     */
    @Override
    public void delete(final Long id, final String userLogin) throws NotFoundException {
        if (userCategoryRepository.existsByIdAndUserLogin(id, userLogin)) {
            throw new NotFoundUserCategoryException();
        }
        userCategoryRepository.deleteById(id);
    }

    /**
     * Find a user by his login
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

}
