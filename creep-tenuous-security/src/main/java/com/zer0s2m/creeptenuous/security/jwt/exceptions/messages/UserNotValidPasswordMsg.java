package com.zer0s2m.creeptenuous.security.jwt.exceptions.messages;

import org.springframework.http.HttpStatus;

public class UserNotValidPasswordMsg {
    private final String massage;
    private final Integer statusCode = HttpStatus.BAD_REQUEST.value();

    public UserNotValidPasswordMsg(String massage) {
        this.massage = massage;
    }

    public String getMassage() {
        return massage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
