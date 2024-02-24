package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;

public record DataTransferredUserApi(

        @Schema(description = "Set Setting - Transfer File Objects to Designated User")
        String login

) { }
