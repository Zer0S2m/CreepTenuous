package com.zer0s2m.CreepTenuous.api.controllers.files.create.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record DataCreateFile(
        @NotNull(message = "Please provide type file (Not NULL)")
        Integer typeFile,

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
