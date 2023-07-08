package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DataControlFileSystemObjectApi(

        @NotNull(message = "Please provide name file system object (Not NULL)")
        @NotBlank(message = "Please provide name file system object")
        @Schema(description = "Name file system object")
        String fileSystemObject

) {
}
