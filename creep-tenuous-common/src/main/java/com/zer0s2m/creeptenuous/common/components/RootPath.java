package com.zer0s2m.creeptenuous.common.components;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * The main system component that provides the starting point for getting and setting the file share root
 */
@Component
public final class RootPath {

    private final String rootPath;

    public RootPath(final @Value("${CT_ROOT_PATH}") @NotNull String rootPath) {
        if (Objects.equals(rootPath.charAt(rootPath.length() - 1), '/')) {
            this.rootPath = rootPath.substring(0, rootPath.length() - 1);
        } else {
            this.rootPath = rootPath;
        }
    }

    public String getRootPath() {
        return this.rootPath;
    }

}
