package CreepTenuous.Directory.BuilderDirectory.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ICollectDirectory {
    List<Path> collect(String path) throws IOException;
}
