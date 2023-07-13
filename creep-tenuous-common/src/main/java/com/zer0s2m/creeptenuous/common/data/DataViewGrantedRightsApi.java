package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DataViewGrantedRightsApi(

        @NotNull(message = "Please specify the name of the file system object (Not NULL)")
        @NotBlank(message = "Please specify the name of the file system object")
        @Schema(description = "The system name of the object in the file system")
        String systemName

) {
}
