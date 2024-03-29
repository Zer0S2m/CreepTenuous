package com.zer0s2m.creeptenuous.api.core.handlers;

import com.zer0s2m.creeptenuous.common.exceptions.NoSuchFileExistsException;
import com.zer0s2m.creeptenuous.common.http.ResponseError;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.NoSuchFileException;

@RestControllerAdvice(basePackages = { "com.zer0s2m.creeptenuous.api" })
public class NoSuchFileExistsAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NoSuchFileException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ResponseError handleNoSuchFileException(@NotNull NoSuchFileException error) {
        return new ResponseError(error.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(NoSuchFileExistsException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ResponseError handleNoSuchFileExistsException(@NotNull NoSuchFileExistsException error) {
        return new ResponseError(error.getMessage(), HttpStatus.NOT_FOUND.value());
    }

}
