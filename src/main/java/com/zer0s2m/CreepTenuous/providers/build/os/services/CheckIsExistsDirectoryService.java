package com.zer0s2m.CreepTenuous.providers.build.os.services;

import com.zer0s2m.CreepTenuous.services.core.Directory;

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
