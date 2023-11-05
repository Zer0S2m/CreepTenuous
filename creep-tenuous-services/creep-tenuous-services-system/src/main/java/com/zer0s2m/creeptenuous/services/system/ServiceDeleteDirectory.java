package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.core.atomic.services.AtomicServiceFileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Maintenance service directory removal
 */
public interface ServiceDeleteDirectory extends AtomicServiceFileSystem {

    /**
     * Delete directory from system
     * @param systemParents parts of the system path - source
     * @param systemName system name directory
     * @throws IOException system error
     */
    void delete(List<String> systemParents, String systemName) throws IOException;

    /**
     * Delete directory from system
     * @param source source path system
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     */
    void delete(Path source) throws IOException;

}
