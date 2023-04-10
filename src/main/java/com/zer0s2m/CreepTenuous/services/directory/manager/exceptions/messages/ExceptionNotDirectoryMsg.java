package com.zer0s2m.CreepTenuous.services.directory.manager.exceptions.messages;

import org.springframework.http.HttpStatus;

public class ExceptionNotDirectoryMsg {
    private final String massage;
    private final Integer statusCode = HttpStatus.NOT_FOUND.value();

    public ExceptionNotDirectoryMsg(String massage) {
        this.massage = massage;
    }

    public String getMassage() {
        return massage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
