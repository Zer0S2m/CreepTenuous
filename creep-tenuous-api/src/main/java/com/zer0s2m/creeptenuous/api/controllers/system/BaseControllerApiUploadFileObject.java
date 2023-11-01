package com.zer0s2m.creeptenuous.api.controllers.system;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

class BaseControllerApiUploadFileObject {

    protected @NotNull Path transfer(@NotNull MultipartFile file) throws IOException {
        Path newPathZipFile = Path.of(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());
        file.transferTo(newPathZipFile);
        return newPathZipFile;
    }

}
