package com.zer0s2m.creeptenuous.common.exceptions;

import com.zer0s2m.creeptenuous.common.enums.UserException;

/**
 * The user does not exist in the system
 */
public class UserNotFoundException extends Exception {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException() {
        this(UserException.USER_NOT_IS_EXISTS.get());
    }
}