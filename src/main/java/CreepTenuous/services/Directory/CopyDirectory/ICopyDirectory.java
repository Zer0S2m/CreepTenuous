package CreepTenuous.services.Directory.CopyDirectory;

import java.io.IOException;

public interface ICopyDirectory {
    void copy(String[] parents, String[] toParents, String nameDirectory) throws IOException;
}
