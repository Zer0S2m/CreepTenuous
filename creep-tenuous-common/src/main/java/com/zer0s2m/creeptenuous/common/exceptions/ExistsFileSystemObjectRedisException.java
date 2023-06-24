package com.zer0s2m.creeptenuous.common.exceptions;

/**
 * Exception for checking for uniqueness of the name in the system under different directory levels
 */
public class ExistsFileSystemObjectRedisException extends Exception {

    public ExistsFileSystemObjectRedisException(String message) {
        super(message);
    }

    public ExistsFileSystemObjectRedisException() {
        this("File object already exists");
    }

}
