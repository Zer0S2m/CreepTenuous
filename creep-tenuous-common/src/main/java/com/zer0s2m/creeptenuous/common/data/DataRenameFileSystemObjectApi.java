package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DataRenameFileSystemObjectApi(

        @NotNull(message = "Please provide name file (system) (Not NULL)")
        @NotBlank(message = "Please provide name file (system)")
        @Schema(description = "System file name")
        String systemName,

        @NotNull(message = "Please provide new file name (Not NULL)")
        @NotBlank(message = "Please provide new file name")
        @Schema(description = "New file name")
        String newRealName

) {
}
