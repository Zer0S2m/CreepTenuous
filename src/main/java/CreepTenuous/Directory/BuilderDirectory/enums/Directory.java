package CreepTenuous.Directory.BuilderDirectory.enums;

public enum Directory {
    SEPARATOR("/"),
    NOT_VALID_LEVEL("Не верно указан уровень вложенности."),
    NOT_FOUND_DIRECTORY("Директории не существует."),
    DIRECTORY_EXISTS("Директория уже существует.");

    private final String msg;

    Directory(String msg) {
        this.msg = msg;
    }

    public String get() {
        return msg;
    }
}
