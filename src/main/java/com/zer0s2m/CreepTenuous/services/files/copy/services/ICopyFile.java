package com.zer0s2m.CreepTenuous.services.files.copy.services;

import com.zer0s2m.CreepTenuous.services.files.move.containers.ContainerMovingFiles;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ICopyFile {
    ContainerMovingFiles copy(String systemNameFile, List<String> systemParents, List<String> systemToParents)
            throws IOException;

    List<ContainerMovingFiles> copy(
            List<String> systemNameFiles,
            List<String> systemParents,
            List<String> systemToParents
    ) throws IOException;

    ContainerMovingFiles copy(Path source, Path target) throws IOException;
}
