package com.zer0s2m.creeptenuous.common.containers;

import java.util.List;

public record ContainerDataBuilderDirectory(
        List<String> partsDirectory,
        Integer levelDirectory,
        List<String> namesSystemFileObject
) { }
