package CreepTenuous.services.directory.builder.exceptions;

import org.springframework.http.HttpStatus;

public class ExceptionNotDirectory {
    private final String massage;
    private final Integer statusCode = HttpStatus.NOT_FOUND.value();

    public ExceptionNotDirectory(String massage) {
        this.massage = massage;
    }

    public String getMassage() {
        return massage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
