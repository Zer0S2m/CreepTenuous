package CreepTenuous.api.core.advice;

import CreepTenuous.api.core.advice.exceptions.messages.FileUploadMaxSizeMsg;
import CreepTenuous.services.files.enums.ExceptionFile;

import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FileUploadExceptionAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<FileUploadMaxSizeMsg> handleMaxSizeException(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
                new FileUploadMaxSizeMsg(ExceptionFile.FILE_LARGE.get())
        );
    }
}