package com.zer0s2m.CreepTenuous.services.user.create.services;

import com.zer0s2m.CreepTenuous.services.user.exceptions.UserAlreadyExistException;
import com.zer0s2m.CreepTenuous.models.User;

public interface IServiceCreateUser {
    /**
     * Create user in system
     * @param user entity user model
     * @return id user
     * @throws UserAlreadyExistException user already exists (by email or login)
     */
    Long create(User user) throws UserAlreadyExistException;
}
