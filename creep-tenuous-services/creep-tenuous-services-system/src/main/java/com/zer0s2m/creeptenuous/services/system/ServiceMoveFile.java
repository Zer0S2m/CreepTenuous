package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.core.atomic.services.AtomicServiceFileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * File move service
 */
public interface ServiceMoveFile extends AtomicServiceFileSystem {

    /**
     * Move file
     * @param systemNameFile system name file
     * @param systemParents parts of the system path - source
     * @param systemToParents parts of the system path - target
     * @return target
     * @throws IOException system error
     */
    Path move(String systemNameFile, List<String> systemParents, List<String> systemToParents) throws IOException;

    /**
     * Move files
     * @param systemNameFiles system names files
     * @param systemParents parts of the system path - source
     * @param systemToParents parts of the system path - target
     * @return target
     * @throws IOException system error
     */
    Path move(List<String> systemNameFiles, List<String> systemParents, List<String> systemToParents)
            throws IOException;

    /**
     * Move file
     * @param source source system path
     * @param target target system path
     * @return target
     * @throws IOException system error
     */
    Path move(Path source, Path target) throws IOException;

}
