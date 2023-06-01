package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DataDeleteFileApi(
        @NotNull(message = "Please provide name file (Not NULL)")
        @NotBlank(message = "Please provide name file")
        @Schema(description = "Real file name")
        String fileName,

        @NotNull(message = "Please provide name file (system) (Not NULL)")
        @NotBlank(message = "Please provide name file (system)")
        @Schema(description = "System file name")
        String systemFileName,

        @NotNull(message = "Please provide path directory (Not NULL)")
        @Schema(description = "Parts of real system paths (directories)")
        List<String> parents,

        @NotNull(message = "Please provide path directory (system) (Not NULL)")
        @Schema(description = "Parts of system paths (directories)")
        List<String> systemParents
) {
    @Override
    public String fileName() {
        return fileName.trim();
    }
}
