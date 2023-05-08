package com.zer0s2m.CreepTenuous.utils.containers;

import java.nio.file.Path;

public record ContainerInfoFileSystemObject(
        Path source,
        Path target,
        String nameFileSystemObject,
        Boolean isFile,
        Boolean isDirectory
) { }
