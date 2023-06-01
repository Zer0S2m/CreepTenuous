package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record DataManagerDirectoryApi(
        @Schema(description = "Nesting level")
        @PositiveOrZero(message = "Please specify the nesting level of the directory")
        Integer level,

        @NotNull(message = "Please provide path current directory (Not NULL)")
        @Schema(description = "Parts of real system paths (directories) - source")
        List<String> parents,

        @NotNull(message = "Please provide path current directory (system) (Not NULL)")
        @Schema(description = "Parts of system paths (directories) - source")
        List<String> systemParents
) { }
