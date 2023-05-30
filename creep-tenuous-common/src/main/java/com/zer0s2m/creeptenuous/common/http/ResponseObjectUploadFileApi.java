package com.zer0s2m.creeptenuous.common.http;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import java.nio.file.Path;

public record ResponseObjectUploadFileApi(
        @Schema(description = "The system name of the object in the file system")
        String realFileName,

        @Schema(description = "The real name of the object in the file system")
        String systemFileName,

        @Schema(description = "Successful upload")
        Boolean success,

        @JsonIgnore
        Path realPath,

        @JsonIgnore
        Path systemPath
) {
}
