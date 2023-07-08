package com.zer0s2m.creeptenuous.api.core.handlers.messages;

import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionFileObjectIsFrozenMsg;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackages = { "com.zer0s2m.creeptenuous.api" })
public class FileObjectIsFrozenExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FileObjectIsFrozenException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionFileObjectIsFrozenMsg> handleFileObjectIsFrozenException(
            @NotNull FileObjectIsFrozenException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ExceptionFileObjectIsFrozenMsg(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

}
