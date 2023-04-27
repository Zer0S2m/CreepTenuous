package com.zer0s2m.CreepTenuous.api.controllers.directory.manager.data;

import java.util.List;


public record DataManagerDirectory(
        List<String> partsDirectory,
        String readyDirectory,
        Integer levelDirectory,
        List<Object> paths
) { }
