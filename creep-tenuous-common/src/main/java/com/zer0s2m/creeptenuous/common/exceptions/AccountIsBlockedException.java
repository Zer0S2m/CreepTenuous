package com.zer0s2m.creeptenuous.common.exceptions;

import com.zer0s2m.creeptenuous.common.enums.UserException;

/**
 * The account is blocked
 */
public class AccountIsBlockedException extends Exception {

    public AccountIsBlockedException(String message) {
        super(message);
    }

    public AccountIsBlockedException() {
        this(UserException.BLOCK_USER.get());
    }

}
