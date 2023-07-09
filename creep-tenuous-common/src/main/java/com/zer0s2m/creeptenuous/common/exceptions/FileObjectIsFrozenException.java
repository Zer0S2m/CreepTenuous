package com.zer0s2m.creeptenuous.common.exceptions;

/**
 * An exception to check if a file object is frozen.
 * <p>Relate to the control part of file objects</p>
 */
public class FileObjectIsFrozenException extends Exception {

    public FileObjectIsFrozenException(String message) {
        super(message);
    }

    public FileObjectIsFrozenException() {
        super("File object is frozen");
    }

}
