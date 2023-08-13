package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DataCreateUserCategoryApi(

        @NotNull(message = "Please specify the name of the title category (Not NULL)")
        @NotBlank(message = "Please specify the name of the title category")
        @Schema(description = "Category name for file objects")
        @Size(max = 128)
        String title

) { }
