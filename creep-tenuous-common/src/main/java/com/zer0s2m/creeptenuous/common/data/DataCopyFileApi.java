package com.zer0s2m.creeptenuous.common.data;

import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public record DataCopyFileApi(
        String nameFile,

        String systemNameFile,

        List<String> nameFiles,

        List<String> systemNameFiles,

        @NotNull(message = "Please provide path current directory (Not NULL)")
        List<String> parents,

        @NotNull(message = "Please provide path current directory (system) (Not NULL)")
        List<String> systemParents,

        @NotNull(message = "Please provide path new directory (Not NULL)")
        List<String> toParents,

        @NotNull(message = "Please provide path new directory (system) (Not NULL)")
        List<String> systemToParents
) {
    @Override
    public String nameFile() {
        return nameFile != null ? nameFile.trim() : null;
    }

    @Override
    public String systemNameFile() {
        return systemNameFile != null ? systemNameFile.trim() : null;
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

    @Override
    public List<String> systemNameFiles() {
        if (systemNameFiles == null) {
            return null;
        }
        List<String> readySystemNameFiles = new ArrayList<>();
        for (String name : systemNameFiles) {
            readySystemNameFiles.add(name.trim());
        }
        return readySystemNameFiles;
    }
}
