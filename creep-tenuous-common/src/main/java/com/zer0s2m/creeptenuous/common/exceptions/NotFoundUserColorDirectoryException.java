package com.zer0s2m.creeptenuous.common.exceptions;

/**
 * Color scheme not found for user directory
 */
public class NotFoundUserColorDirectoryException extends NotFoundException {

    public NotFoundUserColorDirectoryException(String message) {
        super(message);
    }

    public NotFoundUserColorDirectoryException() {
        this("Color scheme not found for user directory");
    }

}
