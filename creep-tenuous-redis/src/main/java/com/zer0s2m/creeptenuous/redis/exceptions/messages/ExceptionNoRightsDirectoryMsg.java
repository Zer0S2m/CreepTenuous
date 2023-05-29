package com.zer0s2m.creeptenuous.redis.exceptions.messages;

import org.springframework.http.HttpStatus;

public class ExceptionNoRightsDirectoryMsg {
    private final String message;
    private final Integer statusCode = HttpStatus.FORBIDDEN.value();

    public ExceptionNoRightsDirectoryMsg(String message) {
        this.message = message;
    }

    public String getMassage() {
        return message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
