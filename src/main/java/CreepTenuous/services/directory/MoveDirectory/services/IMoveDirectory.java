package CreepTenuous.services.directory.MoveDirectory.services;

import java.io.IOException;

public interface IMoveDirectory {
    void move(String[] parents, String[] toParents, String nameDirectory) throws IOException;
}
