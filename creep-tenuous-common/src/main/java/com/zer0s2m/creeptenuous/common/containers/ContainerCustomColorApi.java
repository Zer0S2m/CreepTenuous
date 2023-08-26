package com.zer0s2m.creeptenuous.common.containers;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContainerCustomColorApi(

        @NotNull(message = "Please specify the color (Not NULL)")
        @NotBlank(message = "Please specify the color")
        @Schema(description = "Color")
        @Size(min = 4)
        @Size(max = 9)
        String color,

        @Schema(description = "ID entity", nullable = true)
        Long id

) { }
