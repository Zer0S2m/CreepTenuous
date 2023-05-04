package com.zer0s2m.CreepTenuous.services.directory.move.containers;

import java.nio.file.Path;

public record ContainerInfoFileSystemObject(
        Path source,
        Path target,
        String nameFileSystemObject,
        Boolean isFile,
        Boolean isDirectory
) { }
