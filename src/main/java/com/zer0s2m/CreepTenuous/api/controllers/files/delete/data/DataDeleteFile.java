package com.zer0s2m.CreepTenuous.api.controllers.files.delete.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DataDeleteFile(
        @NotNull(message = "Please provide name file (Not NULL)")
        @NotBlank(message = "Please provide name file")
        String nameFile,

        @NotNull(message = "Please provide path directory (Not NULL)")
        List<String> parents
) {
    @Override
    public String nameFile() {
        return nameFile.trim();
    }
}
