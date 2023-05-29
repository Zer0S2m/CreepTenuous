package com.zer0s2m.creeptenuous.security.jwt.exceptions.messages;

import org.springframework.http.HttpStatus;

public class NoValidJwtRefreshTokenMsg {
    private final String message;
    private final Integer statusCode = HttpStatus.UNAUTHORIZED.value();

    public NoValidJwtRefreshTokenMsg(String message) {
        this.message = message;
    }

    public String getMassage() {
        return message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
