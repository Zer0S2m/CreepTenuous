package com.zer0s2m.creeptenuous.security.jwt.exceptions.messages;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotFoundMsg {

    private final String message;

    private final Integer status;

    public UserNotFoundMsg(String message) {
        this.message = message;
        this.status = HttpStatus.UNAUTHORIZED.value();
    }

    public UserNotFoundMsg(String message, Integer statusCode) {
        this.message = message;
        this.status = statusCode;
    }

}
