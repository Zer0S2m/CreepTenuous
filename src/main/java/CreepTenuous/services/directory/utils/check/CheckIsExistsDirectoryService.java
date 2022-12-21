package CreepTenuous.services.directory.utils.check;

import CreepTenuous.services.directory.builderDirectory.enums.Directory;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public interface CheckIsExistsDirectoryService {
    default void checkDirectory(
            Path path
    ) throws NoSuchFileException {
        if (!Files.exists(path)) {
            throw new NoSuchFileException(Directory.NOT_FOUND_DIRECTORY.get());
        }
    }
}
