package CreepTenuous.api.controllers.directory.moveDirectory.forms;

import java.util.List;

public class FormMoveDirectoryApi {
    private final List<String> parents;
    private final List<String> toParents;
    private final String nameDirectory;

    public FormMoveDirectoryApi(List<String> parents, List<String> toParents, String nameDirectory) {
        this.parents = parents;
        this.toParents = toParents;
        this.nameDirectory = nameDirectory;
    }

    public final List<String> getParents() {
        return this.parents;
    }

    public final List<String> getToParents() {
        return this.toParents;
    }

    public final String getNameDirectory() {
        return this.nameDirectory;
    }
}
