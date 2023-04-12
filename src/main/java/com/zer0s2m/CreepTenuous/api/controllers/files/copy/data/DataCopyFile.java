package com.zer0s2m.CreepTenuous.api.controllers.files.copy.data;

import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public record DataCopyFile(
        String nameFile,

        List<String> nameFiles,

        @NotNull(message = "Please provide path current directory (Not NULL)")
        List<String> parents,

        @NotNull(message = "Please provide path new directory (Not NULL)")
        List<String> toParents
) {
    @Override
    public String nameFile() {
        return nameFile != null ? nameFile.trim() : null;
    }

    @Override
    public List<String> nameFiles() {
        if (nameFiles == null) {
            return null;
        }
        List<String> readyNameFiles = new ArrayList<>();
        for (String name : nameFiles) {
            readyNameFiles.add(name.trim());
        }
        return readyNameFiles;
    }
}
