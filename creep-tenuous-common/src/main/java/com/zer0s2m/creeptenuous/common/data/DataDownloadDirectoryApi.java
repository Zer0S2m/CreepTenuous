package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DataDownloadDirectoryApi(
        @NotNull(message = "Please provide path directory (Not NULL)")
        @Schema(description = "Parts of real system paths (directories)")
        List<String> parents,

        @NotNull(message = "Please provide path directory (system) (Not NULL)")
        @Schema(description = "Parts of system paths (directories)")
        List<String> systemParents,

        @NotNull(message = "Please provide name folder (Not NULL)")
        @NotBlank(message = "Please provide name folder")
        @Schema(description = "Real directory name")
        String directoryName,

        @NotNull(message = "Please provide name folder (system) (Not NULL)")
        @NotBlank(message = "Please provide name folder (system)")
        @Schema(description = "System directory name")
        String systemDirectoryName
) { }
