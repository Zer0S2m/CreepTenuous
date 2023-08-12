package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DataEditCustomColorApi(

        @NotNull(message = "Please provide id (Not NULL)")
        @Min(value = 1, message = "Please enter more than zero")
        @Schema(description = "Id entity")
        Long id,

        @NotNull(message = "Please specify the color (Not NULL)")
        @NotBlank(message = "Please specify the color")
        @Schema(description = "Color")
        @Size(min = 4)
        @Size(max = 9)
        String color

) { }
