package com.zer0s2m.CreepTenuous.services.directory.move.containers;

import java.nio.file.Path;
import java.util.List;

public record ContainerMoveDirectory(
        Path target,
        Path source,
        List<ContainerInfoFileSystemObject> attached,
        String systemNameDirectory
) { }
