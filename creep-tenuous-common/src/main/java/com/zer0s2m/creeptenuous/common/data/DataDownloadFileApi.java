package com.zer0s2m.creeptenuous.common.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DataDownloadFileApi(
        @NotNull(message = "Please provide path directory (Not NULL)")
        List<String> parents,

        @NotNull(message = "Please provide path directory (system) (Not NULL)")
        List<String> systemParents,

        @NotNull(message = "Please provide name file (Not NULL)")
        @NotBlank(message = "Please provide name file")
        String fileName,

        @NotNull(message = "Please provide name file (system) (Not NULL)")
        @NotBlank(message = "Please provide name file (system)")
        String systemFileName
) { }
