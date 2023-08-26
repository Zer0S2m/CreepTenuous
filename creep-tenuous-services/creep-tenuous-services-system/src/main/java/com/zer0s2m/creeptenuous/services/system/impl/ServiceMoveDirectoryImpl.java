package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataMoveDirectory;
import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.enums.MethodMoveDirectory;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.context.nio.file.FilesContextAtomic;
import com.zer0s2m.creeptenuous.core.atomic.handlers.impl.ServiceFileSystemExceptionHandlerOperationMove;
import com.zer0s2m.creeptenuous.core.atomic.services.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.services.system.ServiceMoveDirectory;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.common.utils.WalkDirectoryInfo;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;

/**
 * Service to serve the movement of directories
 */
@ServiceFileSystem("service-move-directory")
@CoreServiceFileSystem(method = "move")
public class ServiceMoveDirectoryImpl implements ServiceMoveDirectory, AtomicServiceFileSystem {

    private final ServiceBuildDirectoryPath buildDirectoryPath;

    private final RootPath rootPath;

    @Autowired
    public ServiceMoveDirectoryImpl(ServiceBuildDirectoryPath buildDirectoryPath, RootPath rootPath) {
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
    @AtomicFileSystem(
            name = "move-directory",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            handler = ServiceFileSystemExceptionHandlerOperationMove.class,
                            exception = IOException.class,
                            operation = ContextAtomicFileSystem.Operations.MOVE
                    )
            }
    )
    public ContainerDataMoveDirectory move(List<String> systemParents, List<String> systemToParents,
                                           String systemNameDirectory, Integer method) throws IOException {
        Path currentPathParents = Paths.get(buildDirectoryPath.build(systemParents));
        Path currentPath = Path.of(currentPathParents.toString(), systemNameDirectory);
        buildDirectoryPath.checkDirectory(currentPath);
        Path createdNewPath = builderDirectory(systemToParents, systemNameDirectory, method);
        List<ContainerInfoFileSystemObject> attached = WalkDirectoryInfo.walkDirectory(currentPath, createdNewPath);
        return new ContainerDataMoveDirectory(
                FilesContextAtomic.move(currentPath, createdNewPath, StandardCopyOption.REPLACE_EXISTING),
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
    protected Path builderDirectory(@NotNull List<String> systemToParents, String systemNameDirectory, Integer method)
            throws IOException {
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
