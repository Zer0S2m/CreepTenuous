package CreepTenuous.services.files.create.exceptions.messages;

import org.springframework.http.HttpStatus;

public class NotFoundTypeFileMsg {
    private final String massage;
    private final Integer statusCode = HttpStatus.BAD_REQUEST.value();

    public NotFoundTypeFileMsg(String massage) {
        this.massage = massage;
    }

    public String getMassage() {
        return massage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
