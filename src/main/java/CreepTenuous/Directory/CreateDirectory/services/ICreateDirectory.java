package CreepTenuous.Directory.CreateDirectory.services;

import CreepTenuous.Directory.BuilderDirectory.enums.Directory;

import java.nio.file.*;

public interface ICreateDirectory {
    void create(String[] parents, String name) throws NoSuchFileException, FileAlreadyExistsException;

    default void checkDirectory(
            Path path,
            Path pathNewDirectory
    ) throws NoSuchFileException, FileAlreadyExistsException {
        if (!Files.exists(path)) {
            throw new NoSuchFileException(Directory.NOT_FOUND_DIRECTORY.get());
        }

        if (Files.exists(pathNewDirectory)) {
            throw new FileAlreadyExistsException(Directory.DIRECTORY_EXISTS.get());
        }
    }
}
