package com.zer0s2m.creeptenuous.common.exceptions;

/**
 * The file system object was not found in the database.
 * <p>Repository the type of the id of the entity the repository manages.</p>
 */
public class NoExistsFileSystemObjectRedisException extends NotFoundException {

    public NoExistsFileSystemObjectRedisException(String message) {
        super(message);
    }

    public NoExistsFileSystemObjectRedisException() {
        this("Not found file system object");
    }

}
