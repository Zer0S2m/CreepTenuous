package com.zer0s2m.creeptenuous.common.exceptions;

/**
 * The right was not found in the database. Or is {@literal null} {@link NullPointerException}
 */
public class NoExistsRightException extends Exception {

    public NoExistsRightException(String message) {
        super(message);
    }

    public NoExistsRightException() {
        this("Not found right");
    }

}
