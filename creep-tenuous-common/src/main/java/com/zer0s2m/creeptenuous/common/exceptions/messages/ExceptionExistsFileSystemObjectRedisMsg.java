package com.zer0s2m.creeptenuous.common.exceptions.messages;

import org.springframework.http.HttpStatus;

@lombok.Getter
public class ExceptionExistsFileSystemObjectRedisMsg {

    private final String message;

    private final Integer status = HttpStatus.BAD_REQUEST.value();

    public ExceptionExistsFileSystemObjectRedisMsg(String message) {
        this.message = message;
    }

}
