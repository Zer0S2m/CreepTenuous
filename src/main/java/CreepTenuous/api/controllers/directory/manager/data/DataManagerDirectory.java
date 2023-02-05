package CreepTenuous.api.controllers.directory.manager.data;

import java.util.List;


public class DataManagerDirectory {
    private final String readyDirectory;
    private final Integer levelDirectory;
    private final List<String> partsDirectory;
    private final List<Object> paths;

    public DataManagerDirectory(
            List<String> partsDirectory,
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

    public List<String> getPartsDirectory() {
        return partsDirectory;
    }

    public List<Object> getPaths() { return paths; }
}
