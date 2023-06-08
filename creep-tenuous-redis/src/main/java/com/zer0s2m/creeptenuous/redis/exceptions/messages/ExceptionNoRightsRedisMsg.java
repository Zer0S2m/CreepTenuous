package com.zer0s2m.creeptenuous.redis.exceptions.messages;

import org.springframework.http.HttpStatus;

public class ExceptionNoRightsRedisMsg {
    private final String message;
    private final Integer statusCode = HttpStatus.FORBIDDEN.value();

    public ExceptionNoRightsRedisMsg(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
