package com.zer0s2m.creeptenuous.common.data;

import com.zer0s2m.creeptenuous.common.enums.MethodMoveDirectory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Data for moving directory
 * @param parents real names directories
 * @param systemParents system names directories
 * @param toParents real names directories
 * @param systemToParents system names directories
 * @param directoryName real name directory
 * @param systemDirectoryName system name directory
 * @param method method moving - {@link MethodMoveDirectory}
 */
public record DataMoveDirectoryApi(

        @NotNull(message = "Please provide source directory (Not NULL)")
        @Schema(description = "Parts of real system paths (directories) - source")
        List<String> parents,

        @NotNull(message = "Please provide source directory (system) (Not NULL)")
        @Schema(description = "Parts of system paths (directories) - source")
        List<String> systemParents,

        @NotNull(message = "Please provide target directory (Not NULL)")
        @Schema(description = "Parts of real system paths (directories) - target")
        List<String> toParents,

        @NotNull(message = "Please provide target directory (system) (Not NULL)")
        @Schema(description = "Parts of system paths (directories) - target")
        List<String> systemToParents,

        @NotNull(message = "Please provide name folder (Not NULL)")
        @NotBlank(message = "Please provide name folder")
        @Schema(description = "Real directory name")
        String directoryName,

        @NotNull(message = "Please provide name folder (system) (Not NULL)")
        @NotBlank(message = "Please provide name folder (system)")
        @Schema(description = "System directory name")
        String systemDirectoryName,

        @NotNull(message = "Please provide method copy (Not NULL)")
        @Schema(description = "Copy method", allowableValues = { "1", "2" }, format = "int32")
        Integer method

) { }