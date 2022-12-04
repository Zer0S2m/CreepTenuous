package CreepTenuous.Directory.BuilderDirectory;

public class BuilderDirectory {
    private final String[] arrPartsDirectory;

    public BuilderDirectory(String[] arrPartsDirectory) {
        this.arrPartsDirectory = arrPartsDirectory;
    }

    private String buildDirectory() {
        StringBuilder rawDirectory = new StringBuilder();
        for (String part : arrPartsDirectory) {
            rawDirectory.append(Directory.SEPARATOR.get()).append(part);
        }
        return rawDirectory.toString();
    }

    public String getDirectory() {
        return buildDirectory();
    }

    public final String[] getArrPartsDirectory() {
        return arrPartsDirectory;
    }
}
