package com.zer0s2m.creeptenuous.security.jwt.exceptions.messages;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotValidPasswordMsg {

    private final String message;

    private final Integer status = HttpStatus.BAD_REQUEST.value();

    public UserNotValidPasswordMsg(String message) {
        this.message = message;
    }

}
