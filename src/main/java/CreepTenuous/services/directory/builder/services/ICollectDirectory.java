package CreepTenuous.services.directory.builder.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ICollectDirectory {
    List<List<Path>> collect(String path) throws IOException;
}