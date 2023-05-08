package com.zer0s2m.CreepTenuous.api.controllers.files.upload.http;

import java.nio.file.Path;

public record DataUploadFile(
        String realFileName,
        String systemFileName,
        Boolean success,
        Path realPath,
        Path systemPath
) {
}
