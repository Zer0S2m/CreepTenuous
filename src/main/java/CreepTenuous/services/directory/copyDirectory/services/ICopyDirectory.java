package CreepTenuous.services.directory.copyDirectory.services;

import java.io.IOException;

public interface ICopyDirectory {
    void copy(String[] parents, String[] toParents, String nameDirectory) throws IOException;
}
