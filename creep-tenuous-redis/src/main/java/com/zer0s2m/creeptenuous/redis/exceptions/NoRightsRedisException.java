package com.zer0s2m.creeptenuous.redis.exceptions;

public class NoRightsRedisException extends RuntimeException {
    public NoRightsRedisException(String message) {
        super(message);
    }

    public NoRightsRedisException() {
        super("Forbidden");
    }
}
