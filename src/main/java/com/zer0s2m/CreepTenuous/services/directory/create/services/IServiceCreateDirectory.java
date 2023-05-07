package com.zer0s2m.CreepTenuous.services.directory.create.services;

import com.zer0s2m.CreepTenuous.services.directory.create.containers.ContainerDataCreatedDirectory;
import com.zer0s2m.CreepTenuous.services.core.Directory;

import java.nio.file.*;
import java.util.List;

public interface IServiceCreateDirectory {
    /**
     * Create directory in system
     * @param systemParents parts of the system path - source
     * @param nameDirectory real system name
     * @return data create directory
     * @throws NoSuchFileException no directory in system
     * @throws FileAlreadyExistsException directory exists
     */
    ContainerDataCreatedDirectory create(List<String> systemParents, String nameDirectory) throws
            NoSuchFileException, FileAlreadyExistsException;

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
