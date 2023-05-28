package com.zer0s2m.creeptenuous.common.exceptions.messages;

import org.springframework.http.HttpStatus;

public class NoSuchFileExists {
    private final String massage;
    private final Integer statusCode = HttpStatus.NOT_FOUND.value();

    public NoSuchFileExists(String massage) {
        this.massage = massage;
    }

    public String getMassage() {
        return massage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
