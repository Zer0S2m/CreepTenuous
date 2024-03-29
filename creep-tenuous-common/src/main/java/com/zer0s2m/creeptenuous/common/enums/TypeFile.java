package com.zer0s2m.creeptenuous.common.enums;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public enum TypeFile {

    TXT(1, "txt"),

    DOCUMENT(2, "docx"),

    EXCEL(3, "xlsx");

    private final Integer code;

    private final String extension;

    TypeFile(Integer code, String extension) {
        this.code = code;
        this.extension = extension;
    }

    public final Integer getCode() {
        return this.code;
    }

    public final String getExtension() {
        return this.extension;
    }

    public static @NotNull List<Integer> getTypesCode() {
        List<Integer> types = new ArrayList<>();

        for (TypeFile type : TypeFile.values()) {
            types.add(type.getCode());
        }

        return types;
    }

    public static @Nullable String getExtension(Integer code) {
        for (TypeFile type : TypeFile.values()) {
            if (Objects.equals(type.getCode(), code)) {
                return type.getExtension();
            }
        }
        return null;
    }

}
