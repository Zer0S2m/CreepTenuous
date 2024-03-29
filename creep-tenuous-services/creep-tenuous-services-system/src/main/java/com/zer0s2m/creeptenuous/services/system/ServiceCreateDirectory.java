package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateDirectory;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.core.atomic.AtomicServiceFileSystem;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

/**
 * Service to serve the creation of directories
 */
public interface ServiceCreateDirectory extends AtomicServiceFileSystem {

    /**
     * Create directory in system
     * @param systemParents parts of the system path - source
     * @param nameDirectory real system name
     * @return data create directory
     * @throws NoSuchFileException no directory in system
     * @throws FileAlreadyExistsException directory exists
     */
    ContainerDataCreateDirectory create(List<String> systemParents, String nameDirectory) throws IOException;

    /**
     * Checking if a directory exists
     * @param pathNewDirectory target path
     * @throws FileAlreadyExistsException directory exists
     */
    default void checkDirectory(Path pathNewDirectory) throws FileAlreadyExistsException {
        if (Files.exists(pathNewDirectory)) {
            throw new FileAlreadyExistsException(Directory.DIRECTORY_EXISTS.get());
        }
    }

}
