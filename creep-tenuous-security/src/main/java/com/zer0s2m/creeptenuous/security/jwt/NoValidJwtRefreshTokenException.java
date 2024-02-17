package com.zer0s2m.creeptenuous.security.jwt;

/**
 * Invalid JWT refresh token
 */
public class NoValidJwtRefreshTokenException extends Exception {

    public NoValidJwtRefreshTokenException(String message) {
        super(message);
    }

}
