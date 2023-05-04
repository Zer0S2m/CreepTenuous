package com.zer0s2m.CreepTenuous.services.directory.move.services.impl;

import com.zer0s2m.CreepTenuous.components.RootPath;
import com.zer0s2m.CreepTenuous.services.core.ServiceFileSystem;
import com.zer0s2m.CreepTenuous.services.directory.move.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.CreepTenuous.services.directory.move.containers.ContainerMoveDirectory;
import com.zer0s2m.CreepTenuous.services.directory.move.enums.MethodMoveDirectory;
import com.zer0s2m.CreepTenuous.services.directory.move.services.IServiceMoveDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;

@ServiceFileSystem("move-directory")
public class ServiceMoveDirectory implements IServiceMoveDirectory {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    private final RootPath rootPath;

    @Autowired
    public ServiceMoveDirectory(ServiceBuildDirectoryPath buildDirectoryPath, RootPath rootPath) {
        this.buildDirectoryPath = buildDirectoryPath;
        this.rootPath = rootPath;
    }

    /**
     * Move directory
     * @param systemParents system path part directories
     * @param systemToParents system path part directories
     * @param systemNameDirectory system name directory
     * @param method method moving {@link MethodMoveDirectory}
     * @return info target and source path
     * @throws IOException error system
     */
    @Override
    public ContainerMoveDirectory move(
            List<String> systemParents,
            List<String> systemToParents,
            String systemNameDirectory,
            Integer method
    ) throws IOException {
        Path currentPathParents = Paths.get(buildDirectoryPath.build(systemParents));
        Path currentPath = Path.of(currentPathParents.toString(), systemNameDirectory);
        Path createdNewPath = builderDirectory(systemToParents, systemNameDirectory, method);
        List<ContainerInfoFileSystemObject> attached = walkDirectory(currentPath, createdNewPath);
        return new ContainerMoveDirectory(
                Files.move(currentPath, createdNewPath, StandardCopyOption.REPLACE_EXISTING),
                currentPath,
                attached,
                systemNameDirectory
        );
    }

    /**
     * Builder system path based method {@link MethodMoveDirectory}
     * @param systemToParents system path part directories
     * @param systemNameDirectory system name directory
     * @param method method moving {@link MethodMoveDirectory}
     * @return ready system path
     * @throws IOException error system
     */
    protected Path builderDirectory(
            List<String> systemToParents,
            String systemNameDirectory,
            Integer method
    ) throws IOException {
        Path newPathDirectory = Path.of(rootPath.getRootPath());
        for (String partPath : systemToParents) {
            newPathDirectory = Path.of(newPathDirectory.toString(), partPath);
        }

        if (Objects.equals(method, MethodMoveDirectory.FOLDER.getMethod())) {
            newPathDirectory = Path.of(newPathDirectory.toString(), systemNameDirectory);
            if (!Files.exists(newPathDirectory)) {
                Files.createDirectories(newPathDirectory);
            }
        }

        return newPathDirectory;
    }
}
