package com.zer0s2m.creeptenuous.common.enums;

public enum ManagerRights {

    SEPARATOR_UNIQUE_KEY("__");

    private final String msg;

    ManagerRights(String msg) {
        this.msg = msg;
    }

    public String get() {
        return msg;
    }

}
