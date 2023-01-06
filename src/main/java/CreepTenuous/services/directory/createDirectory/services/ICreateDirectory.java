package CreepTenuous.services.directory.createDirectory.services;

import CreepTenuous.services.directory.builderDirectory.enums.Directory;

import java.nio.file.*;
import java.util.List;

public interface ICreateDirectory {
    void create(List<String> parents, String name) throws NoSuchFileException, FileAlreadyExistsException;

    default void checkDirectory(Path pathNewDirectory) throws FileAlreadyExistsException {
        if (Files.exists(pathNewDirectory)) {
            throw new FileAlreadyExistsException(Directory.DIRECTORY_EXISTS.get());
        }
    }
}
