package com.zer0s2m.creeptenuous.common.enums;

public enum UserException {

    USER_NOT_IS_EXISTS("User is not found."),

    BLOCK_SELF_USER("You can't block yourself"),

    BLOCK_USER("The account is blocked"),

    USER_NOT_VALID_PASSWORD("Incorrect password.");

    private final String msg;

    UserException(String msg) {
        this.msg = msg;
    }

    public String get() {
        return msg;
    }

}
