package CreepTenuous.Directory.BuilderDirectory;

public enum Directory {
    SEPARATOR("/");

    private final String sep;

    Directory(String sep) {
        this.sep = sep;
    }

    public String get() {
        return sep;
    }
}
