package com.zer0s2m.creeptenuous.redis.exceptions;

/**
 * There are no rights to perform an operation on a file system object
 */
public class NoRightsRedisException extends RuntimeException {

    public NoRightsRedisException(String message) {
        super(message);
    }

    public NoRightsRedisException() {
        super("Forbidden");
    }

}
