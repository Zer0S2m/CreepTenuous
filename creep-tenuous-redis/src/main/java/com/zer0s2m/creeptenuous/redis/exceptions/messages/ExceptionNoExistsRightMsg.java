package com.zer0s2m.creeptenuous.redis.exceptions.messages;

import org.springframework.http.HttpStatus;

public class ExceptionNoExistsRightMsg {

    private final String message;

    private final Integer status = HttpStatus.NOT_FOUND.value();

    public ExceptionNoExistsRightMsg(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatus() {
        return status;
    }

}
