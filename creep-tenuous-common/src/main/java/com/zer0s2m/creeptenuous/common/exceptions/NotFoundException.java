package com.zer0s2m.creeptenuous.common.exceptions;

/**
 * The exception is for not found objects in the system
 */
public abstract class NotFoundException extends Exception {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException() {
        this("Not found object");
    }

}
