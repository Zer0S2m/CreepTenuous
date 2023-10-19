package com.zer0s2m.creeptenuous.common.exceptions.messages;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExceptionNoExistsFileSystemObjectRedisMsg {

    private final String message;

    private final Integer status = HttpStatus.NOT_FOUND.value();

    public ExceptionNoExistsFileSystemObjectRedisMsg(String message) {
        this.message = message;
    }

}
