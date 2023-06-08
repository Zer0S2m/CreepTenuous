package com.zer0s2m.creeptenuous.common.http;

import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import io.swagger.v3.oas.annotations.media.Schema;

public record ResponseCreateRightUserApi(

        @Schema(description = "The system name of the object in the file system")
        String systemName,

        @Schema(description = "Login of the user for which the right will be created")
        String loginUser,

        @Schema(description = "The name of the operation to resolve the interaction")
        OperationRights right
) { }
