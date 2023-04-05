package CreepTenuous.services.directory.create.exceptions.messages;

import org.springframework.http.HttpStatus;

public class ExceptionDirectoryExistsMsg {
    private final String massage;
    private final Integer statusCode = HttpStatus.BAD_REQUEST.value();

    public ExceptionDirectoryExistsMsg(String massage) {
        this.massage = massage;
    }

    public String getMassage() {
        return massage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
