package com.zer0s2m.creeptenuous.common.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DataDownloadDirectoryApi(
        @NotNull(message = "Please provide source directory (Not NULL)")
        List<String> parents,

        @NotNull(message = "Please provide source directory (system) (Not NULL)")
        List<String> systemParents,

        @NotNull(message = "Please provide name folder (Not NULL)")
        @NotBlank(message = "Please provide name folder")
        String directory,

        @NotNull(message = "Please provide name folder (system) (Not NULL)")
        @NotBlank(message = "Please provide name folder (system)")
        String systemDirectory
) { }
