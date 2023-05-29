package com.zer0s2m.creeptenuous.common.exceptions.messages;

import org.springframework.http.HttpStatus;

public class NoSuchFileExists {
    private final String message;
    private final Integer statusCode = HttpStatus.NOT_FOUND.value();

    public NoSuchFileExists(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
