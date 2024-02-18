package com.zer0s2m.creeptenuous.core.services.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateDirectory;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.FilesContextAtomic;
import com.zer0s2m.creeptenuous.core.atomic.ServiceFileSystemExceptionHandlerOperationCreate;
import com.zer0s2m.creeptenuous.core.atomic.Distribution;
import com.zer0s2m.creeptenuous.core.services.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.core.services.ServiceCreateDirectory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

/**
 * Service to serve the creation of directories
 */
@ServiceFileSystem("service-create-directory")
@CoreServiceFileSystem(method = "create")
public class ServiceCreateDirectoryImpl implements ServiceCreateDirectory {

    private final Logger logger = LogManager.getLogger(ServiceCreateDirectory.class);

    private final ServiceBuildDirectoryPath buildDirectoryPath = new ServiceBuildDirectoryPath();

    /**
     * Create directory in system
     * @param systemParents parts of the system path - source
     * @param nameDirectory real system name
     * @return data create directory
     * @throws NoSuchFileException no directory in system
     * @throws FileAlreadyExistsException directory exists
     */
    @Override
    @AtomicFileSystem(
            name = "create-directory",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            exception = IOException.class,
                            handler = ServiceFileSystemExceptionHandlerOperationCreate.class,
                            operation = ContextAtomicFileSystem.Operations.CREATE
                    )
            }
    )
    public ContainerDataCreateDirectory create(List<String> systemParents, String nameDirectory) throws IOException {
        Path path = Paths.get(buildDirectoryPath.build(systemParents));

        String newNameDirectory = Distribution.getUUID();
        Path pathNewDirectory = Path.of(path.toString(), newNameDirectory);

        checkDirectory(pathNewDirectory);

        logger.info(String.format(
                "Creating a directory: source [%s] system name [%s]",
                path, newNameDirectory
        ));

        FilesContextAtomic.createDirectory(pathNewDirectory);

        return new ContainerDataCreateDirectory(nameDirectory, newNameDirectory, pathNewDirectory);
    }

}
