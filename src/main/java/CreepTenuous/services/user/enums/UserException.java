package CreepTenuous.services.user.enums;

public enum UserException {
    USER_NOT_IS_EXISTS("Пользователь не найден"),
    USER_NOT_VALID_PASSWORD("Неверный пароль");

    private final String msg;

    UserException(String msg) {
        this.msg = msg;
    }

    public String get() {
        return msg;
    }
}
