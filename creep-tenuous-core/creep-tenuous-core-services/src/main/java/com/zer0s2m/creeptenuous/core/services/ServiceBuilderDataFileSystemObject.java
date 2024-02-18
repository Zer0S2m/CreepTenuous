package com.zer0s2m.creeptenuous.core.services;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataSystemFileObject;

import java.nio.file.Path;
import java.util.List;

/**
 * Service for collecting information about the directory and its objects
 */
public interface ServiceBuilderDataFileSystemObject {

    /**
     * Get data system file objects
     * @param paths source directories
     * @return data system file objects
     */
    ContainerDataSystemFileObject build(List<List<Path>> paths);

}
