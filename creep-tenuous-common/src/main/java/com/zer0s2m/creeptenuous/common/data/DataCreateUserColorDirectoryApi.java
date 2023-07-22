package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DataCreateUserColorDirectoryApi(

        @NotNull(message = "Please provide name file system object (Not NULL)")
        @NotBlank(message = "Please provide name file system object")
        @Schema(description = "Name file system object")
        String fileSystemObject,

        @NotNull(message = "Please specify the color (Not NULL)")
        @NotBlank(message = "Please specify the color")
        @Schema(description = "Color for directory")
        @Size(min = 4)
        @Size(max = 9)
        String color

) {
}
