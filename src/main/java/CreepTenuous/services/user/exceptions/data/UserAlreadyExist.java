package CreepTenuous.services.user.exceptions.data;

import org.springframework.http.HttpStatus;

public class UserAlreadyExist {
    private final String massage;
    private final Integer statusCode = HttpStatus.BAD_REQUEST.value();

    public UserAlreadyExist(String massage) {
        this.massage = massage;
    }

    public String getMassage() {
        return massage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
