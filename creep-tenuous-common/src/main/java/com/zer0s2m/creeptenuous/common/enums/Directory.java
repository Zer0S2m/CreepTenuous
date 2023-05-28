package com.zer0s2m.creeptenuous.common.enums;

public enum Directory {
    SEPARATOR("/"),
    NOT_VALID_LEVEL("Wrong nesting level."),
    NOT_FOUND_DIRECTORY("Directory does not exist."),
    DIRECTORY_EXISTS("Directory already exists."),
    TYPE_APPLICATION_ZIP("application/zip"),
    EXTENSION_FILE_ZIP(".zip"),
    THREAD_NAME_UNPACKING_DIRECTORY("unpacking-directory-zip"),
    BYTES_COLLECT_ZIP(4096),
    BYTES_UNPACKING_ZIP(4096);

    private final String msg;
    private final Integer msgInt;

    Directory(String msg) {
        this.msg = msg;
        this.msgInt = -1;
    }

    Directory(Integer msg) {
        this.msg = "";
        this.msgInt = msg;
    }

    public String get() {
        return msg;
    }

    public Integer getInt() {
        return msgInt;
    }
}
