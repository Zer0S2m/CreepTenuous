package com.zer0s2m.CreepTenuous.api.controllers.directory.copy.data;

import java.util.List;

public record FormCopyDirectoryApi(List<String> parents, String nameDirectory, List<String> toParents) {
}
