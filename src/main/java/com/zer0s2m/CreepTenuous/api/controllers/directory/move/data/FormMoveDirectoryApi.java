package com.zer0s2m.CreepTenuous.api.controllers.directory.move.data;

import java.util.List;

public record FormMoveDirectoryApi(List<String> parents, List<String> toParents, String nameDirectory) {
}
