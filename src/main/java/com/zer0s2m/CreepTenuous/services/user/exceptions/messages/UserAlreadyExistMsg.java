package com.zer0s2m.CreepTenuous.services.user.exceptions.messages;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistMsg {
    private final String massage;
    private final Integer statusCode = HttpStatus.BAD_REQUEST.value();

    public UserAlreadyExistMsg(String massage) {
        this.massage = massage;
    }

    public String getMassage() {
        return massage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
