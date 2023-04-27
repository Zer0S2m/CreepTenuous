package com.zer0s2m.CreepTenuous.services.directory.manager.containers;

import java.util.List;

public record ContainerDataFiles(
        List<Object> objects,
        List<String> namesDirectory
) { }
