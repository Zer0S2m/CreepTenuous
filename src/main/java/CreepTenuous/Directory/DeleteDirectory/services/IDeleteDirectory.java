package CreepTenuous.Directory.DeleteDirectory.services;

import CreepTenuous.Directory.BuilderDirectory.enums.Directory;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public interface IDeleteDirectory {
    void delete(String[] parents, String name) throws NoSuchFileException;

    default void checkDirectory(
            Path path
    ) throws NoSuchFileException {
        if (!Files.exists(path)) {
            throw new NoSuchFileException(Directory.NOT_FOUND_DIRECTORY.get());
        }
    }
}
