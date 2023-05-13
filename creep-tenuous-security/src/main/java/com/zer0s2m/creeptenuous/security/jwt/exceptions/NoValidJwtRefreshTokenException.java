package com.zer0s2m.creeptenuous.security.jwt.exceptions;

public class NoValidJwtRefreshTokenException extends Exception {
    public NoValidJwtRefreshTokenException(String message) {
        super(message);
    }
}
