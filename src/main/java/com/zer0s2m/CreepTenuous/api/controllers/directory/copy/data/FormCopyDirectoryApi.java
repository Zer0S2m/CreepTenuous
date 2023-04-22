package com.zer0s2m.CreepTenuous.api.controllers.directory.copy.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FormCopyDirectoryApi(
        @NotNull(message = "Please provide source directory (Not NULL)")
        List<String> parents,

        @NotNull(message = "Please provide target directory (Not NULL)")
        List<String> toParents,

        @NotNull(message = "Please provide name folder (Not NULL)")
        @NotBlank(message = "Please provide name folder")
        String nameDirectory,

        @NotNull(message = "Please provide method copy (Not NULL)")
        Integer method
) { }

