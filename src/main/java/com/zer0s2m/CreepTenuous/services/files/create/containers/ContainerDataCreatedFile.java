package com.zer0s2m.CreepTenuous.services.files.create.containers;

import java.nio.file.Path;

public record ContainerDataCreatedFile(
        String realNameFile,
        String systemNameFile,
        Path realPathFile,
        Path systemPathFile
) { }
