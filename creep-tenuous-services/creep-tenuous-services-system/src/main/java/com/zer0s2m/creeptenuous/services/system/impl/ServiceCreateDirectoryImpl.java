package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateDirectory;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.context.nio.file.FilesContextAtomic;
import com.zer0s2m.creeptenuous.core.atomic.handlers.impl.ServiceFileSystemExceptionHandlerOperationCreate;
import com.zer0s2m.creeptenuous.core.atomic.services.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.services.Distribution;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.ServiceCreateDirectory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

/**
 * Service to serve the creation of directories
 */
@ServiceFileSystem("service-create-directory")
@CoreServiceFileSystem(method = "create")
public class ServiceCreateDirectoryImpl implements ServiceCreateDirectory, AtomicServiceFileSystem {

    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceCreateDirectoryImpl(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

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
        FilesContextAtomic.createDirectory(pathNewDirectory);

        return new ContainerDataCreateDirectory(nameDirectory, newNameDirectory, pathNewDirectory);
    }

}
