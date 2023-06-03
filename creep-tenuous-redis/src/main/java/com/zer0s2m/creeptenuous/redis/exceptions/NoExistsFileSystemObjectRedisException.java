package com.zer0s2m.creeptenuous.redis.exceptions;

import org.springframework.data.repository.Repository;

/**
 * The file system object was not found in the database.
 * <p>{@link Repository} the type of the id of the entity the repository manages.</p>
 */
public class NoExistsFileSystemObjectRedisException extends Exception {

    public NoExistsFileSystemObjectRedisException(String message) {
        super(message);
    }

    public NoExistsFileSystemObjectRedisException() {
        this("Not found file system object");
    }

}
