package com.zer0s2m.creeptenuou.sservices.user;

import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.common.exceptions.UserAlreadyExistException;

/**
 * {@link User} model maintenance service
 */
public interface ServiceCreateUser {

    /**
     * Create user in system
     * @param user entity user model
     * @return id user
     * @throws UserAlreadyExistException user already exists (by email or login)
     */
    Long create(User user) throws UserAlreadyExistException;

}
