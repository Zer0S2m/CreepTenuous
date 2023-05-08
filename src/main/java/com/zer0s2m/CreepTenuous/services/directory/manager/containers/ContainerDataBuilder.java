package com.zer0s2m.CreepTenuous.services.directory.manager.containers;

import java.util.List;

public record ContainerDataBuilder(
        List<String> partsDirectory,
        Integer levelDirectory,
        List<String> namesSystemFileObject
) { }
