package com.zer0s2m.creeptenuous.common.exceptions.messages;

import org.springframework.http.HttpStatus;

public class ExceptionDirectoryExistsMsg {
    private final String message;
    private final Integer statusCode = HttpStatus.BAD_REQUEST.value();

    public ExceptionDirectoryExistsMsg(String message) {
        this.message = message;
    }

    public String getMassage() {
        return message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
