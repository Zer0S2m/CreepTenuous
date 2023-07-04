package com.zer0s2m.creeptenuous.integration.implants.http;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResponseServerUnavailableApi(

        @Schema(description = "Code status", example = "503")
        Integer status,

        @Schema(
                description = "Error message",
                example = "The server is not ready to handle the request"
        )
        String message
) { }
