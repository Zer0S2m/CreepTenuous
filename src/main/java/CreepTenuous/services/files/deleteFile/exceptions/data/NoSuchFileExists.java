package CreepTenuous.services.files.deleteFile.exceptions.data;

import org.springframework.http.HttpStatus;

public class NoSuchFileExists {
    private final String massage;
    private final Integer statusCode = HttpStatus.NOT_FOUND.value();

    public NoSuchFileExists(String massage) {
        this.massage = massage;
    }

    public String getMassage() {
        return massage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
