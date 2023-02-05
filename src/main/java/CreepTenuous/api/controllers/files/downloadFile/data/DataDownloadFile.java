package CreepTenuous.api.controllers.files.downloadFile.data;

import org.springframework.lang.Nullable;

import java.util.List;

public class DataDownloadFile {
    private final List<String> parents;
    private final String filename;

    public DataDownloadFile(@Nullable List<String> parents, @Nullable String filename) {
        this.parents = parents;
        this.filename = filename;
    }

    public List<String> getParents() {
        return parents;
    }

    public String getFilename() {
        return filename;
    }
}
