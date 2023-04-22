package com.zer0s2m.CreepTenuous.providers.jwt.exceptions;

public class NoValidJwtRefreshTokenException extends Exception {
    public NoValidJwtRefreshTokenException(String message) {
        super(message);
    }
}
