package CreepTenuous.Directory.BuilderDirectory.services;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public interface IBuilderDataFile {
    List<Object> build(ArrayList<List<Path>> paths);
}
