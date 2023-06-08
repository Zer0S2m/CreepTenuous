package com.zer0s2m.creeptenuous.redis.exceptions.messages;

import org.springframework.http.HttpStatus;

public class ExceptionAddRightsYourselfMsg {

    private final String message;

    private final Integer statusCode = HttpStatus.BAD_REQUEST.value();

    public ExceptionAddRightsYourselfMsg(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

}
