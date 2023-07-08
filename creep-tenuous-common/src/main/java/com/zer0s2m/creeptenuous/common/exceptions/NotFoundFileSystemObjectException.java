package com.zer0s2m.creeptenuous.common.exceptions;

/**
 * The exception is for not found file system objects
 */
public class NotFoundFileSystemObjectException extends NotFoundException {

    public NotFoundFileSystemObjectException(String message) {
        super(message);
    }

    public NotFoundFileSystemObjectException() {
        this("Not found file system object");
    }

}
