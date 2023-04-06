package CreepTenuous.api.controllers.files.move.data;

import java.util.List;

public record DataMoveFile(
        String nameFile,
        List<String> parents,
        List<String> toParents
) {  }
