package com.zer0s2m.creeptenuous.common.enums;

/**
 * Base directories in the OS
 */
public enum BaseCatalog {

    DOCUMENTS("Documents"),

    VIDEO("Video"),

    MUSIC("Music"),

    IMAGES("Images");

    private final String message;

    BaseCatalog(String message) {
        this.message = message;
    }

    public final String get() {
        return this.message;
    }

}
