package CreepTenuous.services.files.create.exceptions.data;

import org.springframework.http.HttpStatus;

public class NotFoundTypeFile {
    private final String massage;
    private final Integer statusCode = HttpStatus.BAD_REQUEST.value();

    public NotFoundTypeFile(String massage) {
        this.massage = massage;
    }

    public String getMassage() {
        return massage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
