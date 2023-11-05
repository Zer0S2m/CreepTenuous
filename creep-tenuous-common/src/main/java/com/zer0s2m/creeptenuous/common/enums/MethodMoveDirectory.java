package com.zer0s2m.creeptenuous.common.enums;

import lombok.Getter;

@Getter
public enum MethodMoveDirectory {

    FOLDER(1),

    CONTENT(2);

    private final Integer method;

    MethodMoveDirectory(Integer method) {
        this.method = method;
    }

}
