package com.zer0s2m.CreepTenuous.services.user.create.services;

import com.zer0s2m.CreepTenuous.services.user.exceptions.UserAlreadyExistException;
import com.zer0s2m.CreepTenuous.models.User;

public interface ICreateUser {
    Long create(User user) throws UserAlreadyExistException;
}
