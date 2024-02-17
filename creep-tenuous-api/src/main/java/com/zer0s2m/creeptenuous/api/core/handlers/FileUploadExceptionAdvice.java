package com.zer0s2m.creeptenuous.api.core.handlers;

import com.zer0s2m.creeptenuous.common.enums.ExceptionFile;
import com.zer0s2m.creeptenuous.common.http.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackages = { "com.zer0s2m.creeptenuous.api" })
public class FileUploadExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseError> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ignored) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
                new ResponseError(ExceptionFile.FILE_LARGE.get(), HttpStatus.EXPECTATION_FAILED.value()));
    }

}