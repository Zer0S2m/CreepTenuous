package CreepTenuous.services.Directory.MoveDirectory.services;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

public interface IMoveDirectory {
    void move(String[] parents, String[] toParents, String nameDirectory) throws IOException;
}
