package com.zer0s2m.creeptenuous.common.enums;

public enum UserAlready {
    USER_EMAIL_EXISTS("email"),
    USER_LOGIN_EXISTS("login"),
    USER_ALREADY_EXISTS("User with this %s already exists.");

    private final String msg;

    UserAlready(String msg) {
        this.msg = msg;
    }

    public String get() {
        return msg;
    }
}
