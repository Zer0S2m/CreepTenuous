package com.zer0s2m.creeptenuous.common.exceptions;

import com.zer0s2m.creeptenuous.common.enums.Directory;

public class FileAlreadyExistsException  extends RuntimeException {
    public FileAlreadyExistsException(String message) {
        super(message);
    }

    public FileAlreadyExistsException() {
        this(Directory.DIRECTORY_EXISTS.get());
    }

}
