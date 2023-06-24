package com.zer0s2m.creeptenuous.api.core.handlers;

import com.zer0s2m.creeptenuous.common.exceptions.NoRightsRedisException;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionNoRightsRedisMsg;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackages = { "com.zer0s2m.creeptenuous.api" })
public class NoRightsDirectoryExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NoRightsRedisException.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    public ExceptionNoRightsRedisMsg handleNoRightsDirectoryException(@NotNull NoRightsRedisException error) {
        return new ExceptionNoRightsRedisMsg(error.getMessage());
    }

}
