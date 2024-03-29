package com.zer0s2m.creeptenuous.common.containers;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContainerCategoryFileSystemObject(

        @NotNull(message = "Please provide id (Not NULL)")
        @Min(value = 1, message = "Please enter more than zero")
        @Schema(description = "ID custom category")
        Long categoryId,

        @NotNull(message = "Please provide name file system object (Not NULL)")
        @NotBlank(message = "Please provide name file system object")
        @Schema(description = "File object name")
        String fileSystemObject

) { }
