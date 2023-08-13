package com.zer0s2m.creeptenuous.common.containers;

import io.swagger.v3.oas.annotations.media.Schema;

public record ContainerDataUserCategory(

        @Schema(description = "Element ID")
        Long id,

        @Schema(description = "Category name for file objects")
        String title,

        @Schema(description = "Category color palette", example = "#fff")
        String color

) {
}
