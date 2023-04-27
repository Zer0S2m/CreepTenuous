package com.zer0s2m.CreepTenuous.services.files.create.containers;

import java.nio.file.Path;

public record ContainerDataCreatedFile(
        String nameFile,
        Path pathFile
) { }
