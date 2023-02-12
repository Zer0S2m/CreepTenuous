package CreepTenuous.services.user.enums;

public enum UserAlready {
    USER_EMAIL_EXISTS("email"),
    USER_LOGIN_EXISTS("login"),
    USER_ALREADY_EXISTS("Пользователь с таким %s уже существует");

    private final String msg;

    UserAlready(String msg) {
        this.msg = msg;
    }

    public String get() {
        return msg;
    }
}
