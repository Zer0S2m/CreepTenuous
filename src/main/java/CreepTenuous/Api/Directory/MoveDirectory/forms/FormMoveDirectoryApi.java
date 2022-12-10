package CreepTenuous.Api.Directory.MoveDirectory.forms;

public class FormMoveDirectoryApi {
    private final String[] parents;
    private final String[] toParents;
    private final String nameDirectory;

    public FormMoveDirectoryApi(String[] parents, String[] toParents, String nameDirectory) {
        this.parents = parents;
        this.toParents = toParents;
        this.nameDirectory = nameDirectory;
    }

    public final String[] getParents() {
        return this.parents;
    }

    public final String[] getToParents() {
        return this.toParents;
    }

    public final String getNameDirectory() {
        return this.nameDirectory;
    }
}
