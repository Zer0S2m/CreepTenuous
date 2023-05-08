package com.zer0s2m.CreepTenuous.services.directory.manager.services;

import com.zer0s2m.CreepTenuous.services.directory.manager.containers.ContainerDataSystemFileObject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public interface IServiceBuilderDataFile {
    /**
     * Get data system file objects
     * @param paths source directories
     * @return data system file objects
     */
    ContainerDataSystemFileObject build(ArrayList<List<Path>> paths);
}
