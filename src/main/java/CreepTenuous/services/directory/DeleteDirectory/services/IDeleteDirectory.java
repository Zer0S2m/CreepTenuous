package CreepTenuous.services.directory.DeleteDirectory.services;

import java.nio.file.NoSuchFileException;
import java.util.List;

public interface IDeleteDirectory {
    void delete(List<String> parents, String name) throws NoSuchFileException;
}
