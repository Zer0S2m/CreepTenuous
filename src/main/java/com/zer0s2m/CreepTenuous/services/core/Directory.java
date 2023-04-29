package com.zer0s2m.CreepTenuous.services.core;

public enum Directory {
    SEPARATOR("/"),
    NOT_VALID_LEVEL("Не верно указан уровень вложенности."),
    NOT_FOUND_DIRECTORY("Директории не существует."),
    DIRECTORY_EXISTS("Директория уже существует."),
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
