package com.zer0s2m.CreepTenuous.services.directory.create.containers;

import java.nio.file.Path;

public record ContainerDataCreatedDirectory(
        String realNameDirectory,
        String systemNameDirectory,
        Path pathDirectory
) { }
