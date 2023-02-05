package CreepTenuous.api.controllers.directory.copy.forms;

import java.util.List;

public class FormCopyDirectoryApi {
    private final List<String> parents;
    private final String nameDirectory;
    private final List<String> toParents;

    public FormCopyDirectoryApi(List<String> parents, String nameDirectory, List<String> toParents) {
        this.parents = parents;
        this.nameDirectory = nameDirectory;
        this.toParents = toParents;
    }

    public List<String> getParents() {
        return this.parents;
    }

    public List<String> getToParents() {
        return this.toParents;
    }

    public String getNameDirectory() {
        return this.nameDirectory;
    }
}
