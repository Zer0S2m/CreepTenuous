package com.zer0s2m.CreepTenuous.services.directory.manager.services;

import java.nio.file.Path;
import java.util.List;

public interface IServiceCollectDirectory {
    /**
     * Collect children in directory
     * @param path system source path
     * @return data: directories and files
     */
    List<List<Path>> collect(String path);
}
