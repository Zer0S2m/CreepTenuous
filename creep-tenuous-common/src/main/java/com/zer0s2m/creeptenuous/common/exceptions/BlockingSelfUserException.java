package com.zer0s2m.creeptenuous.common.exceptions;

import com.zer0s2m.creeptenuous.common.enums.UserException;

/**
 * Blocking self users
 */
public class BlockingSelfUserException extends Exception {

    public BlockingSelfUserException(String message) {
        super(message);
    }

    public BlockingSelfUserException() {
        this(UserException.BLOCK_SELF_USER.get());
    }

}
