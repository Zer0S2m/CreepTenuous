package com.zer0s2m.CreepTenuous.services.core.exceptions.messages;

import org.springframework.http.HttpStatus;

public class ExceptionNoRightsCreateDirectoryMsg {
    private final String massage;
    private final Integer statusCode = HttpStatus.FORBIDDEN.value();

    public ExceptionNoRightsCreateDirectoryMsg(String massage) {
        this.massage = massage;
    }

    public String getMassage() {
        return massage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
