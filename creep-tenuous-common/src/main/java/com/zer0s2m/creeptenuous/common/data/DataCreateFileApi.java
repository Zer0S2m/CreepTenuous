package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.Contract;

import java.util.List;

public record DataCreateFileApi(

        @NotNull(message = "Please provide type file (Not NULL)")
        @Schema(description = "File type", allowableValues = { "1", "2", "3" }, defaultValue = "1")
        Integer typeFile,

        @NotNull(message = "Please provide name file (Not NULL)")
        @NotBlank(message = "Please provide name file")
        @Schema(description = "Directory name")
        String fileName,

        @NotNull(message = "Please provide path directory (Not NULL)")
        @Schema(description = "Parts of real system paths (directories)")
        List<String> parents,

        @NotNull(message = "Please provide path directory (system) (Not NULL)")
        @Schema(description = "Parts of system paths (directories)")
        List<String> systemParents

) {

    @Contract(pure = true)
    @Override
    public @org.jetbrains.annotations.NotNull String fileName() {
        return fileName.trim();
    }

}
