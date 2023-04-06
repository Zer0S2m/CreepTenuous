package CreepTenuous.api.controllers.files.move.data;

import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public record DataMoveFile(
        String nameFile,
        List<String> nameFiles,
        @NotNull List<String> parents,
        @NotNull List<String> toParents
) {
    @Override
    public String nameFile() {
        return nameFile != null ? nameFile.trim() : null;
    }

    @Override
    public List<String> nameFiles() {
        List<String> readyNameFiles = new ArrayList<>();
        for (String name : nameFiles) {
            readyNameFiles.add(name.trim());
        }
        return readyNameFiles;
    }
}
