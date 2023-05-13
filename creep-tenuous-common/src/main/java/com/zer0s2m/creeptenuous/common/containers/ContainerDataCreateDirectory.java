package com.zer0s2m.creeptenuous.common.containers;

import java.nio.file.Path;

public record ContainerDataCreateDirectory(
        String realNameDirectory,
        String systemNameDirectory,
        Path pathDirectory
) { }
