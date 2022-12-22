package CreepTenuous.services.directory.copyDirectory.services;

import java.io.IOException;
import java.util.List;

public interface ICopyDirectory {
    void copy(List<String> parents, List<String> toParents, String nameDirectory) throws IOException;
}
