package com.zer0s2m.CreepTenuous.services.files.upload.containers;

import java.nio.file.Path;

public record ContainerDataUploadFile(
        String nameFile,
        Path pathFile
) { }
