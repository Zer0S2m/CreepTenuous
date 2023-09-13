package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCopyFile;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.context.nio.file.FilesContextAtomic;
import com.zer0s2m.creeptenuous.core.atomic.handlers.impl.ServiceFileSystemExceptionHandlerOperationCopy;
import com.zer0s2m.creeptenuous.core.atomic.services.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.ServiceCopyFile;
import com.zer0s2m.creeptenuous.services.system.utils.UtilsFiles;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * File copy service
 */
@ServiceFileSystem("service-copy-file")
@CoreServiceFileSystem(method = "copy")
public class ServiceCopyFileImpl implements ServiceCopyFile, AtomicServiceFileSystem {

    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceCopyFileImpl(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    /**
     * Copy file
     * @param systemNameFile system name file
     * @param systemParents parts of the system path - source
     * @param systemToParents parts of the system path - target
     * @return info copy files
     * @throws IOException system error
     */
    @Override
    @AtomicFileSystem(
            name = "copy-file",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            exception = IOException.class,
                            handler = ServiceFileSystemExceptionHandlerOperationCopy.class,
                            operation = ContextAtomicFileSystem.Operations.COPY
                    )
            }
    )
    public ContainerDataCopyFile copy(String systemNameFile, List<String> systemParents,
                                      List<String> systemToParents) throws IOException {
        Path currentPath = Paths.get(buildDirectoryPath.build(systemParents), systemNameFile);
        String newSystemNameFile = UtilsFiles.getNewFileName(systemNameFile);
        Path createdNewPath = Paths.get(buildDirectoryPath.build(systemToParents), newSystemNameFile);

        return copy(currentPath, createdNewPath);
    }

    /**
     * Copy file
     * @param systemNameFiles system names file
     * @param systemParents parts of the system path - source
     * @param systemToParents parts of the system path - target
     * @return info copy files
     * @throws IOException system error
     */
    @Override
    @AtomicFileSystem(
            name = "copy-file",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            exception = IOException.class,
                            handler = ServiceFileSystemExceptionHandlerOperationCopy.class,
                            operation = ContextAtomicFileSystem.Operations.COPY
                    )
            }
    )
    public List<ContainerDataCopyFile> copy(@NotNull List<String> systemNameFiles, List<String> systemParents,
                                            List<String> systemToParents) throws IOException {
        List<ContainerDataCopyFile> containers = new ArrayList<>();
        for (String name : systemNameFiles) {
            containers.add(copy(name, systemParents, systemToParents));
        }
        return containers;
    }

    /**
     * Copy file
     * @param source source system path
     * @param target target system path
     * @return info copy file
     * @throws IOException system error
     */
    @Override
    @AtomicFileSystem(
            name = "copy-file",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            exception = IOException.class,
                            handler = ServiceFileSystemExceptionHandlerOperationCopy.class,
                            operation = ContextAtomicFileSystem.Operations.COPY
                    )
            }
    )
    public ContainerDataCopyFile copy(Path source, Path target) throws IOException {
        Path newTarget = FilesContextAtomic.copy(source, target, REPLACE_EXISTING);
        return new ContainerDataCopyFile(
            newTarget, newTarget.getFileName().toString()
        );
    }

}
