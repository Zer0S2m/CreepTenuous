package com.zer0s2m.creeptenuous.common.exceptions.messages;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundTypeFileMsg {

    private final String message;

    private final Integer status = HttpStatus.BAD_REQUEST.value();

    public NotFoundTypeFileMsg(String message) {
        this.message = message;
    }

}
