package com.zer0s2m.creeptenuous.common.components;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The main system component that provides the starting point for getting and setting the file share root
 */
@Getter
public final class RootPath {

    private final String rootPath = setRootPath();

    @Contract(pure = true)
    private @NotNull String setRootPath() {
        String rootPath = System.getenv("CT_ROOT_PATH");
        if (Objects.equals(rootPath.charAt(rootPath.length() - 1), '/')) {
            return rootPath.substring(0, rootPath.length() - 1);
        } else {
            return rootPath;
        }
    }

}
