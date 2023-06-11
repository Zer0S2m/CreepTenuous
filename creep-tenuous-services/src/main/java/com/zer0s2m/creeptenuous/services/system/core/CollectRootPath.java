package com.zer0s2m.creeptenuous.services.system.core;

import java.nio.file.NoSuchFileException;

/**
 * Service for assembling the paths of file system objects. Use in end endpoints
 */
public interface CollectRootPath {

    /**
     * Collect path to filesystem object and raise exception if object not found
     * @param rawPath raw string path
     * @return clean string path
     * @throws NoSuchFileException file that does not exist.
     */
    String collect(String rawPath) throws NoSuchFileException;

}
