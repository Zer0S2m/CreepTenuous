package CreepTenuous.api.controllers.files.delete.data;

import java.util.List;

public class DataDeleteFile {
    private final String nameFile;
    private final List<String> parents;

    public DataDeleteFile(String nameFile, List<String> parents) {
        this.nameFile = nameFile;
        this.parents = parents;
    }

    public String getNameFile() {
        return this.nameFile;
    }

    public List<String> getParents() {
        return this.parents;
    }
}
