package CreepTenuous.api.controllers.directory.download.data;

import java.util.List;

public class DataDownloadDirectory {
    private final List<String> parents;
    private final String directory;

    public DataDownloadDirectory(List<String> parents, String directory) {
        this.parents = parents;
        this.directory = directory;
    }

    public List<String> getParents() {
        return parents;
    }

    public String getDirectory() {
        return directory;
    }
}
