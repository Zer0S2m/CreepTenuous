package com.zer0s2m.CreepTenuous.providers.build.os.services;

import com.zer0s2m.CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import com.zer0s2m.CreepTenuous.services.core.ExceptionFile;

import java.nio.file.Files;
import java.nio.file.Path;

public interface CheckIsExistsFileService {
    default void checkFile(Path path) throws NoSuchFileExistsException {
        if (!Files.exists(path)) {
            throw new NoSuchFileExistsException(ExceptionFile.FILE_NOT_EXISTS.get());
        }
    }
}
