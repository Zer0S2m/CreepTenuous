package com.zer0s2m.creeptenuous.common.containers;

import java.nio.file.Path;

public record ContainerDataMoveFile(
        Path target,
        String systemNameFile
) { }
