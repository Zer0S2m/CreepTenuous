package com.zer0s2m.creeptenuous.api.core.handlers.messages;

import org.springframework.http.HttpStatus;

public class FileUploadMaxSizeMsg {
    private final String message;
    private final Integer statusCode = HttpStatus.BAD_REQUEST.value();

    public FileUploadMaxSizeMsg(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}