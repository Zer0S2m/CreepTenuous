package com.zer0s2m.creeptenuous.common.http;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResponseCreateFileApi(
        @Schema(description = "Real file name")
        String realFileName,

        @Schema(description = "System file name")
        String systemFileName
) { }
