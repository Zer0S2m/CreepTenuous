package CreepTenuous.services.files.enums;

public enum ExceptionFile {
    NOT_FOUND_TYPE_FILE("Тип файла не найден."),
    FILE_ALREADY_EXISTS("Файл с таким названием уже существует."),
    FILE_NOT_EXISTS("Файл не найден."),
    FILE_LARGE("Файл превышает максимальный размер");

    private final String massage;

    ExceptionFile(String massage) {
        this.massage = massage;
    }

    public final String get() {
        return this.massage;
    }
}
