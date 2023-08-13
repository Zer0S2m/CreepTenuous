package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record DataMoveFileApi(

        @Schema(description = "Real file name")
        String fileName,

        @Schema(description = "System file name")
        String systemFileName,

        @Schema(description = "Real file names")
        List<String> nameFiles,

        @Schema(description = "System file names")
        List<String> systemNameFiles,

        @NotNull(message = "Please provide path current directory (Not NULL)")
        @Schema(description = "Parts of real system paths (directories) - source")
        List<String> parents,

        @NotNull(message = "Please provide path current directory (system) (Not NULL)")
        @Schema(description = "Parts of system paths (directories) - source")
        List<String> systemParents,

        @NotNull(message = "Please provide path new directory (Not NULL)")
        @Schema(description = "Parts of real system paths (directories) - target")
        List<String> toParents,

        @NotNull(message = "Please provide path new directory (system) (Not NULL)")
        @Schema(description = "Parts of system paths (directories) - target")
        List<String> systemToParents

) {

    @Contract(pure = true)
    @Override
    public @Nullable String fileName() {
        return fileName != null ? fileName.trim() : null;
    }

    @Contract(pure = true)
    @Override
    public @Nullable String systemFileName() {
        return systemFileName != null ? systemFileName.trim() : null;
    }

    @Override
    public @Nullable List<String> nameFiles() {
        if (nameFiles == null) {
            return null;
        }
        List<String> readyNameFiles = new ArrayList<>();
        for (String name : nameFiles) {
            readyNameFiles.add(name.trim());
        }
        return readyNameFiles;
    }

    @Override
    public @Nullable List<String> systemNameFiles() {
        if (systemNameFiles == null) {
            return null;
        }
        List<String> readySystemNameFiles = new ArrayList<>();
        for (String name : systemNameFiles) {
            readySystemNameFiles.add(name.trim());
        }
        return readySystemNameFiles;
    }

}
