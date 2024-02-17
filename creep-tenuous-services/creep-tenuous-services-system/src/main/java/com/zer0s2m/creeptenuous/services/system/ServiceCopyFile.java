package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataCopyFile;
import com.zer0s2m.creeptenuous.core.atomic.AtomicServiceFileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * File copy service
 */
public interface ServiceCopyFile extends AtomicServiceFileSystem {

    /**
     * Copy file
     * @param systemNameFile system name file
     * @param systemParents parts of the system path - source
     * @param systemToParents parts of the system path - target
     * @return info copy files
     * @throws IOException system error
     */
    ContainerDataCopyFile copy(String systemNameFile, List<String> systemParents, List<String> systemToParents)
            throws IOException;

    /**
     * Copy file
     * @param systemNameFiles system names file
     * @param systemParents parts of the system path - source
     * @param systemToParents parts of the system path - target
     * @return info copy files
     * @throws IOException system error
     */
    List<ContainerDataCopyFile> copy(List<String> systemNameFiles, List<String> systemParents,
                                     List<String> systemToParents) throws IOException;

    /**
     * Copy file
     * @param source source system path
     * @param target target system path
     * @return info copy file
     * @throws IOException system error
     */
    ContainerDataCopyFile copy(Path source, Path target) throws IOException;

}
