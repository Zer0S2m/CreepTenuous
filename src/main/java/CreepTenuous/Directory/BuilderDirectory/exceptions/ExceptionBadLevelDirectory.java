package CreepTenuous.Directory.BuilderDirectory.exceptions;

import org.springframework.http.HttpStatus;

public class ExceptionBadLevelDirectory {
    private final String massage;
    private final Integer statusCode = HttpStatus.BAD_REQUEST.value();

    public ExceptionBadLevelDirectory(String massage) {
        this.massage = massage;
    }

    public String getMassage() {
        return massage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
