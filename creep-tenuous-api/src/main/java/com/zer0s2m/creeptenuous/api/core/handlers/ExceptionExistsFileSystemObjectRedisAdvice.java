package com.zer0s2m.creeptenuous.api.core.handlers;

import com.zer0s2m.creeptenuous.common.exceptions.ExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.common.http.ResponseError;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackages = { "com.zer0s2m.creeptenuous.api" })
public class ExceptionExistsFileSystemObjectRedisAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ExistsFileSystemObjectRedisException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseError> handleExistsFileSystemObjectRedisException(
            @NotNull ExistsFileSystemObjectRedisException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseError(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

}
