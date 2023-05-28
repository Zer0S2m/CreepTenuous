package com.zer0s2m.creeptenuous.common.containers;

import java.nio.file.Path;

public record ContainerInfoFileSystemObject(
        Path source,
        Path target,
        String nameFileSystemObject,
        Boolean isFile,
        Boolean isDirectory
) { }
