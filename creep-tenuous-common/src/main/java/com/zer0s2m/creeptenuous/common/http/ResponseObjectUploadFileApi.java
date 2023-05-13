package com.zer0s2m.creeptenuous.common.http;

import java.nio.file.Path;

public record ResponseObjectUploadFileApi(
        String realFileName,
        String systemFileName,
        Boolean success,
        Path realPath,
        Path systemPath
) {
}
