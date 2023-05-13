package com.zer0s2m.creeptenuous.redis.exceptions.messages;

import org.springframework.http.HttpStatus;

public class ExceptionNoRightsDirectoryMsg {
    private final String massage;
    private final Integer statusCode = HttpStatus.FORBIDDEN.value();

    public ExceptionNoRightsDirectoryMsg(String massage) {
        this.massage = massage;
    }

    public String getMassage() {
        return massage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
