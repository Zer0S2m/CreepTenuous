package com.zer0s2m.creeptenuous.starter.test.mock;

public enum User {
    ROLE_USER("ROLE_USER"),
    LOGIN("user_test");

    private final String msg;

    User(String msg) {
        this.msg = msg;
    }

    public String get() {
        return msg;
    }
}
