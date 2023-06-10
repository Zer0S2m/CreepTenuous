package com.zer0s2m.creeptenuous.common.http;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResponseCreateDirectoryApi(
        @Schema(description = "Real directory name")
        String realDirectoryName,

        @Schema(description = "System directory name")
        String systemDirectoryName
) { }
