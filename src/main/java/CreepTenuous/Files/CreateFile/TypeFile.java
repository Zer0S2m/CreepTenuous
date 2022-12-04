package CreepTenuous.Files.CreateFile;

public enum TypeFile {
    TXT(1),
    DOCUMENT(2),
    EXCEL(3);

    private final Integer code;

    TypeFile(Integer code) {
        this.code = code;
    }

    public final Integer get() {
        return this.code;
    }
}
