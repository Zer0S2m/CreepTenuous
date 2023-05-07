package com.zer0s2m.CreepTenuous.services.files.copy.services;

import com.zer0s2m.CreepTenuous.services.files.move.containers.ContainerMovingFiles;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface IServiceCopyFile {
    /**
     * Copy file
     * @param systemNameFile system name file
     * @param systemParents parts of the system path - source
     * @param systemToParents parts of the system path - target
     * @return info copy files
     * @throws IOException system error
     */
    ContainerMovingFiles copy(String systemNameFile, List<String> systemParents, List<String> systemToParents)
            throws IOException;

    /**
     * Copy file
     * @param systemNameFiles system names file
     * @param systemParents parts of the system path - source
     * @param systemToParents parts of the system path - target
     * @return info copy files
     * @throws IOException system error
     */
    List<ContainerMovingFiles> copy(
            List<String> systemNameFiles,
            List<String> systemParents,
            List<String> systemToParents
    ) throws IOException;

    /**
     * Copy file
     * @param source source system path
     * @param target target system path
     * @return info copy file
     * @throws IOException system error
     */
    ContainerMovingFiles copy(Path source, Path target) throws IOException;
}
