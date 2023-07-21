package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DataDeleteUserCategoryApi(

        @NotNull(message = "Please provide id (Not NULL)")
        @Min(value = 1, message = "Please enter more than zero")
        @Schema(description = "ID category")
        Long id

) {
}
