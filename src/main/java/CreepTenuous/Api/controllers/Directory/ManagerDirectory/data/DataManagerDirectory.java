package CreepTenuous.Api.controllers.Directory.ManagerDirectory.data;

import java.util.List;


public class DataManagerDirectory {
    private final String readyDirectory;
    private final Integer levelDirectory;
    private final String[] partsDirectory;
    private final List<Object> paths;

    public DataManagerDirectory(
            String[] partsDirectory,
            String readyDirectory,
            Integer levelDirectory,
            List<Object> paths
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

    public List<Object> getPaths() { return paths; }
}
