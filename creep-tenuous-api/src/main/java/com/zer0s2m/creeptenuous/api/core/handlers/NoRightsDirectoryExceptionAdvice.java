package com.zer0s2m.creeptenuous.api.core.handlers;

import com.zer0s2m.creeptenuous.redis.exceptions.NoRightsDirectoryException;
import com.zer0s2m.creeptenuous.redis.exceptions.messages.ExceptionNoRightsDirectoryMsg;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackages = { "com.zer0s2m.creeptenuous.api" })
public class NoRightsDirectoryExceptionAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(NoRightsDirectoryException.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    public ExceptionNoRightsDirectoryMsg handleNoRightsDirectoryException(NoRightsDirectoryException error) {
        return new ExceptionNoRightsDirectoryMsg(error.getMessage());
    }
}
