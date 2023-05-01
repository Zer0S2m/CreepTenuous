package com.zer0s2m.CreepTenuous.services.files.copy.services.impl;

import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.core.ServiceFileSystem;
import com.zer0s2m.CreepTenuous.services.files.copy.services.ICopyFile;
import com.zer0s2m.CreepTenuous.services.files.move.containers.ContainerMovingFiles;
import com.zer0s2m.CreepTenuous.utils.UtilsFiles;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@ServiceFileSystem("copy-file")
public class ServiceCopyFile implements ICopyFile {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceCopyFile(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    @Override
    public ContainerMovingFiles copy(
            String systemNameFile,
            List<String> systemParents,
            List<String> systemToParents
    )
            throws IOException {
        Path currentPath = Paths.get(buildDirectoryPath.build(systemParents), systemNameFile);

        String newSystemNameFile = UtilsFiles.getNewFileName(systemNameFile);

        Path createdNewPath = Paths.get(buildDirectoryPath.build(systemToParents), newSystemNameFile);

        return copy(currentPath, createdNewPath);
    }

    @Override
    public List<ContainerMovingFiles> copy(
            List<String> systemNameFiles,
            List<String> systemParents,
            List<String> systemToParents
    )
            throws IOException {
        List<ContainerMovingFiles> containers = new ArrayList<>();
        for (String name : systemNameFiles) {
            containers.add(copy(name, systemParents, systemToParents));
        }
        return containers;
    }

    @Override
    public ContainerMovingFiles copy(Path source, Path target) throws IOException {
        Path newTarget = Files.copy(source, target, REPLACE_EXISTING);
        return new ContainerMovingFiles(
            newTarget, newTarget.getFileName().toString()
        );
    }
}
