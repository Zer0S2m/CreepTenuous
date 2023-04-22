package com.zer0s2m.CreepTenuous.api.controllers.files.download.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.util.List;

public record DataDownloadFile(
        @NotNull(message = "Please provide path directory (Not NULL)")
        List<String> parents,

        @NotNull(message = "Please provide name file (Not NULL)")
        @NotBlank(message = "Please provide name file")
        String filename
) {
    public DataDownloadFile(@Nullable List<String> parents, @Nullable String filename) {
        this.parents = parents;
        this.filename = filename;
    }
}
