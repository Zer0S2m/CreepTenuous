package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataSystemFileObject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public interface ServiceBuilderDataFileSystemObject {
    /**
     * Get data system file objects
     * @param paths source directories
     * @return data system file objects
     */
    ContainerDataSystemFileObject build(ArrayList<List<Path>> paths);
}
