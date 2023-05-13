package com.zer0s2m.creeptenuous.common.enums;

public enum UserException {
    USER_NOT_IS_EXISTS("User is not found."),
    USER_NOT_VALID_PASSWORD("Incorrect password.");

    private final String msg;

    UserException(String msg) {
        this.msg = msg;
    }

    public String get() {
        return msg;
    }
}
