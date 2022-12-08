package CreepTenuous.Api.Directory.CreateDirectory.forms;

public class FormCreateDirectoryApi {
    private final String[] parents;
    private final String name;

    public FormCreateDirectoryApi(String[] parents, String name) {
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
