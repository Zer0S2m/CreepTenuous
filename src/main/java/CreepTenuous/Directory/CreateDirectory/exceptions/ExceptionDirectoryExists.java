package CreepTenuous.Directory.CreateDirectory.exceptions;

import org.springframework.http.HttpStatus;

public class ExceptionDirectoryExists {
    private final String massage;
    private final Integer statusCode = HttpStatus.BAD_REQUEST.value();

    public ExceptionDirectoryExists(String massage) {
        this.massage = massage;
    }

    public String getMassage() {
        return massage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
