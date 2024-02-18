package com.zer0s2m.creeptenuous.core.services;

import java.nio.file.Path;
import java.util.List;

/**
 * Service for collecting information about the directory and its objects
 */
public interface ServiceCollectDirectory {

    /**
     * Collect children in directory
     * @param path system source path
     * @return data: directories and files
     */
    List<List<Path>> collect(String path);

}
