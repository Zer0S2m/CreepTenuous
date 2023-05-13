package com.zer0s2m.creeptenuous.common.containers;

import java.util.List;

public record ContainerDataSystemFileObject(
        List<Object> objects,
        List<String> namesDirectory
) { }
