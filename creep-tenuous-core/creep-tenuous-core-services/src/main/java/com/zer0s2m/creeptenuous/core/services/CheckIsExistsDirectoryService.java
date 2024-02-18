package com.zer0s2m.creeptenuous.core.services;

import com.zer0s2m.creeptenuous.common.enums.Directory;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * An interface for serving file system object paths. Use in end endpoints
 */
public interface CheckIsExistsDirectoryService {

    /**
     * Check the path of a file system object for its existence
     * @param path source path
     * @throws NoSuchFileException file that does not exist.
     */
    default void checkDirectory(Path path) throws NoSuchFileException {
        if (!Files.exists(path)) {
            throw new NoSuchFileException(Directory.NOT_FOUND_DIRECTORY.get());
        }
    }

}
