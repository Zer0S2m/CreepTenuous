package com.zer0s2m.creeptenuous.common.exceptions.messages;

import org.springframework.http.HttpStatus;

public class ExceptionAccountIsBlockedMsg {

    private final String message;

    private final Integer status = HttpStatus.UNAUTHORIZED.value();

    public ExceptionAccountIsBlockedMsg(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatus() {
        return status;
    }

}