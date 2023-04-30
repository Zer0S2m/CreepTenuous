package com.zer0s2m.CreepTenuous.api.controllers.files.upload.http;

import java.nio.file.Path;

public record ResponseUploadFile(
        String realFileName,
        String systemFileName,
        Boolean success,
        Path realPath,
        Path systemPath
) {
}
