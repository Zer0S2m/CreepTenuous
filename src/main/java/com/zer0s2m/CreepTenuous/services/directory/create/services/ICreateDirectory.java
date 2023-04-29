package com.zer0s2m.CreepTenuous.services.directory.create.services;

import com.zer0s2m.CreepTenuous.services.directory.create.containers.ContainerDataCreatedDirectory;
import com.zer0s2m.CreepTenuous.services.core.Directory;

import java.nio.file.*;
import java.util.List;

public interface ICreateDirectory {
    ContainerDataCreatedDirectory create(List<String> systemParents, String nameDirectory) throws
            NoSuchFileException, FileAlreadyExistsException;

    default void checkDirectory(Path pathNewDirectory) throws FileAlreadyExistsException {
        if (Files.exists(pathNewDirectory)) {
            throw new FileAlreadyExistsException(Directory.DIRECTORY_EXISTS.get());
        }
    }
}
