package com.zer0s2m.CreepTenuous.api.controllers.directory.move.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Data for moving directory
 * @param parents real names directories
 * @param systemParents system names directories
 * @param toParents real names directories
 * @param systemToParents system names directories
 * @param nameDirectory real name directory
 * @param systemNameDirectory system name directory
 * @param method method moving - {@link com.zer0s2m.CreepTenuous.services.directory.move.enums.MethodMoveDirectory}
 */
public record FormMoveDirectoryApi(
        @NotNull(message = "Please provide source directory (Not NULL)")
        List<String> parents,

        @NotNull(message = "Please provide source directory (system) (Not NULL)")
        List<String> systemParents,

        @NotNull(message = "Please provide target directory (Not NULL)")
        List<String> toParents,

        @NotNull(message = "Please provide target directory (system) (Not NULL)")
        List<String> systemToParents,

        @NotNull(message = "Please provide name folder (Not NULL)")
        @NotBlank(message = "Please provide name folder")
        String nameDirectory,

        @NotNull(message = "Please provide name folder (system) (Not NULL)")
        @NotBlank(message = "Please provide name folder (system)")
        String systemNameDirectory,

        @NotNull(message = "Please provide method copy (Not NULL)")
        Integer method
) { }
