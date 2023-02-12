package CreepTenuous.api.core.security.jwt.messages;

import org.springframework.http.HttpStatus;

public class UserNotFoundMsg {
    private final String massage;
    private final Integer statusCode = HttpStatus.BAD_REQUEST.value();

    public UserNotFoundMsg(String massage) {
        this.massage = massage;
    }

    public String getMassage() {
        return massage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
