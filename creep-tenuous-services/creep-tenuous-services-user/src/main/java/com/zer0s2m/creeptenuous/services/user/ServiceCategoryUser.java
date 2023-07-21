package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataUserCategory;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;

import java.util.List;

/**
 * Basic interface for implementing work with an entity - a category for a user
 */
public interface ServiceCategoryUser {

    /**
     * Get all custom categories
     * @param userLogin user login
     * @return entities
     */
    List<ContainerDataUserCategory> getAll(final String userLogin);

    /**
     * Creating a category for file objects
     * @param title Category name
     * @param userLogin login user
     * @return entity data
     * @throws UserNotFoundException user not exists
     */
    ContainerDataUserCategory create(final String title, final String userLogin) throws UserNotFoundException;

    /**
     * Update custom category by id
     * @param id ID entity
     * @param title new title user category
     * @param userLogin user login
     * @throws NotFoundException not found user category
     */
    void edit(Long id, final String title, final String userLogin) throws NotFoundException;

    /**
     * Delete comment file system object
     * @param id id comment. Must not be {@literal null}.
     * @param userLogin user login. Must not be {@literal null}.
     * @throws NotFoundException not found user category
     */
    void delete(final Long id, final String userLogin) throws NotFoundException;

}
