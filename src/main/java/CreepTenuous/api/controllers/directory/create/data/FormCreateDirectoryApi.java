package CreepTenuous.api.controllers.directory.create.data;

import java.util.List;

public record FormCreateDirectoryApi(List<String> parents, String name) {
}
