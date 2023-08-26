package com.zer0s2m.creeptenuous.common.enums;

public enum ExceptionFile {

    NOT_FOUND_TYPE_FILE("File type not found."),

    FILE_ALREADY_EXISTS("A file with the same name already exists."),

    FILE_NOT_EXISTS("File not found."),

    FILE_LARGE("File exceeds the maximum size.");


    private final String message;

    ExceptionFile(String message) {
        this.message = message;
    }

    public final String get() {
        return this.message;
    }

}
