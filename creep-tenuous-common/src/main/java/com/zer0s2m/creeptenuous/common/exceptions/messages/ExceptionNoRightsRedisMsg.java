package com.zer0s2m.creeptenuous.common.exceptions.messages;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExceptionNoRightsRedisMsg {
    private final String message;
    private final Integer status = HttpStatus.FORBIDDEN.value();

    public ExceptionNoRightsRedisMsg(String message) {
        this.message = message;
    }

}
