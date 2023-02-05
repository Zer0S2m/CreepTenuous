package CreepTenuous.api.controllers.directory.delete.forms;

import java.util.List;

public class FormDeleteDirectoryApi {
    private final List<String> parents;
    private final String name;

    public FormDeleteDirectoryApi(List<String> parents, String name) {
        this.parents = parents;
        this.name = name;
    }

    public final List<String> getParents() {
        return this.parents;
    }

    public final String getName() {
        return this.name;
    }
}
