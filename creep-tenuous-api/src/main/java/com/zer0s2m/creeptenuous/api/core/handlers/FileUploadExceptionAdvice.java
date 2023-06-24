package com.zer0s2m.creeptenuous.api.core.handlers;

import com.zer0s2m.creeptenuous.api.core.handlers.messages.FileUploadMaxSizeMsg;
import com.zer0s2m.creeptenuous.common.enums.ExceptionFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackages = { "com.zer0s2m.creeptenuous.api" })
public class FileUploadExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<FileUploadMaxSizeMsg> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
                new FileUploadMaxSizeMsg(ExceptionFile.FILE_LARGE.get()));
    }

}