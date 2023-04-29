package com.zer0s2m.CreepTenuous.services.directory.create.exceptions;

import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;

public class FileAlreadyExistsException  extends RuntimeException {
    public FileAlreadyExistsException(String message) {
        super(message);
    }

    public FileAlreadyExistsException() {
        super(Directory.DIRECTORY_EXISTS.get());
    }
}
