package com.zer0s2m.CreepTenuous.api.controllers.files.create.data;

import java.util.List;

public record DataCreateFile(Integer typeFile, String nameFile, List<String> parents) {
    @Override
    public String nameFile() {
        return nameFile.trim();
    }
}
