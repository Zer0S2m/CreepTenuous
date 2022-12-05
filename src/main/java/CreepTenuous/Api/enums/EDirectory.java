package CreepTenuous.Api.enums;

public enum EDirectory {
    NOT_VALID_LEVEL("Не верно указан уровень вложенности."),
    NOT_VALID_DIRECTORY("Директории не существует.");

    private final String msg;

    EDirectory(String msg) {
        this.msg = msg;
    }

    public final String get() {
        return this.msg;
    }
}
