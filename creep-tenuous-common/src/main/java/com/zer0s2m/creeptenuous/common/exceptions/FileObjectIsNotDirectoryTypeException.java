package com.zer0s2m.creeptenuous.common.exceptions;

/**
 * File object is not a directory type
 */
public class FileObjectIsNotDirectoryTypeException extends Exception {

    public FileObjectIsNotDirectoryTypeException(String message) {
        super(message);
    }

    public FileObjectIsNotDirectoryTypeException() {
        this("File object is not a directory type");
    }

}
