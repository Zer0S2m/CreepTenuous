package CreepTenuous.api.core.advice.messages;

import org.springframework.http.HttpStatus;

public class FileUploadMaxSize {
    private final String message;
    private final Integer statusCode = HttpStatus.BAD_REQUEST.value();

    public FileUploadMaxSize(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}