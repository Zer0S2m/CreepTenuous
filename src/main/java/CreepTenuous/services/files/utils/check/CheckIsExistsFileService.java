package CreepTenuous.services.files.utils.check;

import CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import CreepTenuous.services.files.enums.ExceptionFile;

import java.nio.file.Files;
import java.nio.file.Path;

public interface CheckIsExistsFileService {
    default void checkFile(Path path) throws NoSuchFileExistsException {
        if (!Files.exists(path)) {
            throw new NoSuchFileExistsException(ExceptionFile.FILE_NOT_EXISTS.get());
        }
    }
}
