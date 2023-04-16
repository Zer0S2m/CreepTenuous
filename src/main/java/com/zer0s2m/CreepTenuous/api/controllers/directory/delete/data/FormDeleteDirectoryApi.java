package com.zer0s2m.CreepTenuous.api.controllers.directory.delete.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FormDeleteDirectoryApi(
        @NotNull(message = "Please provide path directory (Not NULL)")
        List<String> parents,

        @NotNull(message = "Please provide name folder (Not NULL)")
        @NotBlank(message = "Please provide name folder")
        String name
) { }
