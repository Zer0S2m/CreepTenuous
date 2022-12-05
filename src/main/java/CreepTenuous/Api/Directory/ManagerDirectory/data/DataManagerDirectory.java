package CreepTenuous.Api.Directory.ManagerDirectory.data;

import java.nio.file.Path;
import java.util.List;

public class DataManagerDirectory {
    private final String readyDirectory;
    private final Integer levelDirectory;
    private final String[] partsDirectory;
    private final List<Path> paths;

    public DataManagerDirectory(
            String[] partsDirectory,
            String readyDirectory,
            Integer levelDirectory,
            List<Path> paths
    ) {
        this.partsDirectory = partsDirectory;
        this.readyDirectory = readyDirectory;
        this.levelDirectory = levelDirectory;
        this.paths = paths;
    }

    public String getReadyDirectory() {
        return readyDirectory;
    }

    public Integer getLevelDirectory() {
        return levelDirectory;
    }

    public String[] getPartsDirectory() {
        return partsDirectory;
    }

    public List<Path> getPaths() { return paths; }
}