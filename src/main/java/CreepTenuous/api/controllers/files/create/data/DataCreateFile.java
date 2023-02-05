package CreepTenuous.api.controllers.files.create.data;

import java.util.List;

public class DataCreateFile {
    private final Integer typeFile;
    private final String nameFile;
    private final List<String> parents;

    public DataCreateFile(Integer typeFile, String nameFile, List<String> parents) {
        this.typeFile = typeFile;
        this.nameFile = nameFile;
        this.parents = parents;
    }

    public Integer typeFile() {
        return typeFile;
    }

    public String nameFile() {
        return nameFile.trim();
    }

    public List<String> getParents() {
        return parents;
    }
}
