package CreepTenuous.services.Directory.DeleteDirectory.services;

import java.nio.file.NoSuchFileException;

public interface IDeleteDirectory {
    void delete(String[] parents, String name) throws NoSuchFileException;
}
