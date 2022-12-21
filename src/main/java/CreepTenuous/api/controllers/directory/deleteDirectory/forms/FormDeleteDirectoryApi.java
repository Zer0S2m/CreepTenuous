package CreepTenuous.api.controllers.directory.deleteDirectory.forms;

public class FormDeleteDirectoryApi {
    private final String[] parents;
    private final String name;

    public FormDeleteDirectoryApi(String[] parents, String name) {
        this.parents = parents;
        this.name = name;
    }

    public final String[] getParents() {
        return this.parents;
    }

    public final String getName() {
        return this.name;
    }
}
