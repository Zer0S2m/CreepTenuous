package com.zer0s2m.creeptenuous.common.containers;

import java.nio.file.Path;
import java.util.List;

public record ContainerDataMoveDirectory(
        Path target,
        Path source,
        List<ContainerInfoFileSystemObject> attached,
        String systemNameDirectory
) { }
