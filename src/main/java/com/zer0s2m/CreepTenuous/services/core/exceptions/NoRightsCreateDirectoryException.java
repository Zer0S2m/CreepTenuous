package com.zer0s2m.CreepTenuous.services.core.exceptions;

public class NoRightsCreateDirectoryException extends RuntimeException {
    public NoRightsCreateDirectoryException(String message) {
        super(message);
    }

    public NoRightsCreateDirectoryException() {
        super("Forbidden");
    }
}
