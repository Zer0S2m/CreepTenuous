package com.zer0s2m.creeptenuous.services.system;

import java.nio.file.Path;
import java.util.List;

public interface ServiceCollectDirectory {
    /**
     * Collect children in directory
     * @param path system source path
     * @return data: directories and files
     */
    List<List<Path>> collect(String path);
}
