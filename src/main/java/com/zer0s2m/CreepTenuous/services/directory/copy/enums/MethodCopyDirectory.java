package com.zer0s2m.CreepTenuous.services.directory.copy.enums;

public enum MethodCopyDirectory {
    FOLDER(1),
    CONTENT(2);

    private final Integer method;

    MethodCopyDirectory(Integer method) {
        this.method = method;
    }

    public Integer getMethod() {
        return method;
    }
}
