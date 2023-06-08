package com.zer0s2m.creeptenuous.security.jwt.exceptions.messages;

import org.springframework.http.HttpStatus;

public class UserNotFoundMsg {
    private final String message;
    private final Integer statusCode;

    public UserNotFoundMsg(String message) {
        this.message = message;
        this.statusCode = HttpStatus.UNAUTHORIZED.value();
    }

    public UserNotFoundMsg(String message, Integer statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}