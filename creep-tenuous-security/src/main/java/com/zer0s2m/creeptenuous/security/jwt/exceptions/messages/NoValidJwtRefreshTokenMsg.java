package com.zer0s2m.creeptenuous.security.jwt.exceptions.messages;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NoValidJwtRefreshTokenMsg {

    private final String message;

    private final Integer status = HttpStatus.UNAUTHORIZED.value();

    public NoValidJwtRefreshTokenMsg(String message) {
        this.message = message;
    }

}
