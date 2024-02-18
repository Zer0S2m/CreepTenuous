package com.zer0s2m.creeptenuous.core.services.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCopyFile;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.FilesContextAtomic;
import com.zer0s2m.creeptenuous.core.atomic.ServiceFileSystemExceptionHandlerOperationCopy;
import com.zer0s2m.creeptenuous.core.services.UtilsFiles;
import com.zer0s2m.creeptenuous.core.services.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.core.services.ServiceCopyFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

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
public class ServiceCopyFileImpl implements ServiceCopyFile  {

    private final Logger logger = LogManager.getLogger(ServiceCopyFile.class);

    private final ServiceBuildDirectoryPath buildDirectoryPath = new ServiceBuildDirectoryPath();

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
        logger.info(String.format(
                "Copying a file: source [%s] target [%s]",
                source, target
        ));

        Path newTarget = FilesContextAtomic.copy(source, target, REPLACE_EXISTING);
        return new ContainerDataCopyFile(
            newTarget, newTarget.getFileName().toString()
        );
    }

}
