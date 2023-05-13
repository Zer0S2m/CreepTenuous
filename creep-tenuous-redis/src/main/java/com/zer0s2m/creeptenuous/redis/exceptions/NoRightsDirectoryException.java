package com.zer0s2m.creeptenuous.redis.exceptions;

public class NoRightsDirectoryException extends RuntimeException {
    public NoRightsDirectoryException(String message) {
        super(message);
    }

    public NoRightsDirectoryException() {
        super("Forbidden");
    }
}
