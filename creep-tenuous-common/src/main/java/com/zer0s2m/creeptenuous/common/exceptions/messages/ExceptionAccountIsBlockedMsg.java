package com.zer0s2m.creeptenuous.common.exceptions.messages;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExceptionAccountIsBlockedMsg {

    private final String message;

    private final Integer status = HttpStatus.UNAUTHORIZED.value();

    public ExceptionAccountIsBlockedMsg(String message) {
        this.message = message;
    }

}
