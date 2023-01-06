package CreepTenuous.services.files.createFile.exceptions.data;

import org.springframework.http.HttpStatus;

public class FileAlreadyExists {
    private final String massage;
    private final Integer statusCode = HttpStatus.BAD_REQUEST.value();

    public FileAlreadyExists(String massage) {
        this.massage = massage;
    }

    public String getMassage() {
        return massage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
