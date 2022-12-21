package CreepTenuous.api.controllers.directory.copyDirectory.forms;

public class FormCopyDirectoryApi {
    private final String[] parents;
    private final String nameDirectory;
    private final String[] toParents;

    public FormCopyDirectoryApi(String[] parents, String nameDirectory, String[] toParents) {
        this.parents = parents;
        this.nameDirectory = nameDirectory;
        this.toParents = toParents;
    }

    public String[] getParents() {
        return this.parents;
    }

    public String[] getToParents() {
        return this.toParents;
    }

    public String getNameDirectory() {
        return this.nameDirectory;
    }
}
