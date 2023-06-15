package com.zer0s2m.creeptenuous.common.data;

import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.validation.constraints.EnumValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DataCreateRightUserApi(

        @NotNull(message = "Please specify the name of the file system object (Not NULL)")
        @NotBlank(message = "Please specify the name of the file system object")
        @Schema(description = "The system name of the object in the file system")
        String systemName,

        @NotNull(message = "Please enter login (Not NULL)")
        @NotBlank(message = "Please enter login")
        @Schema(description = "Login of the user for which the right will be created")
        String loginUser,

        @EnumValidator(enumClass = OperationRights.class, message = "Please enter right")
        @Schema(
                description = "The name of the operation to resolve the interaction",
                allowableValues = { "MOVE", "COPY", "UPLOAD", "DOWNLOAD", "CREATE", "DELETE", "SHOW", "RENAME", "ALL" }
        )
        String right

) { }
