package com.zer0s2m.creeptenuous.security.jwt.exceptions.messages;

import org.springframework.http.HttpStatus;

public class UserNotFoundMsg {

    private final String message;
    private final Integer status;

    public UserNotFoundMsg(String message) {
        this.message = message;
        this.status = HttpStatus.UNAUTHORIZED.value();
    }

    public UserNotFoundMsg(String message, Integer statusCode) {
        this.message = message;
        this.status = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatus() {
        return status;
    }

}
