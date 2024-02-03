package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.FilesContextAtomic;
import com.zer0s2m.creeptenuous.core.atomic.ServiceFileSystemExceptionHandlerOperationDelete;
import com.zer0s2m.creeptenuous.services.system.ServiceDeleteDirectory;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Maintenance service directory removal
 */
@ServiceFileSystem("delete-directory")
@CoreServiceFileSystem(method = "delete")
public class ServiceDeleteDirectoryImpl implements ServiceDeleteDirectory {

    private final Logger logger = LogManager.getLogger(ServiceDeleteDirectory.class);

    private final ServiceBuildDirectoryPath buildDirectoryPath = new ServiceBuildDirectoryPath();

    /**
     * Delete directory from system
     * @param systemParents parts of the system path - source
     * @param systemName system name directory
     * @throws IOException system error
     */
    @Override
    @AtomicFileSystem(
            name = "delete-directory",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            exception = IOException.class,
                            handler = ServiceFileSystemExceptionHandlerOperationDelete.class,
                            operation = ContextAtomicFileSystem.Operations.DELETE
                    )
            }
    )
    public void delete(List<String> systemParents, String systemName) throws IOException {
        Path path = Paths.get(buildDirectoryPath.build(systemParents), systemName);

        logger.info(String.format(
                "Deleting a directory: source [%s]",
                path
        ));

        if (Files.isDirectory(path)) {
            try (Stream<Path> pathStream = Files.walk(path)) {
                pathStream
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(FilesContextAtomic::deleteNoException);
            }
        }
    }

    /**
     * Delete directory from system
     * @param source source path system
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     */
    @Override
    @AtomicFileSystem(
            name = "delete-directory",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            exceptionMulti = IOException.class,
                            isExceptionMulti = true,
                            handler = ServiceFileSystemExceptionHandlerOperationDelete.class,
                            operation = ContextAtomicFileSystem.Operations.DELETE
                    )
            }
    )
    public void delete(Path source) throws IOException {
        logger.info(String.format(
                "Deleting a directory: source [%s]",
                source
        ));

        if (Files.isDirectory(source)) {
            try (Stream<Path> pathStream = Files.walk(source)) {
                pathStream
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(FilesContextAtomic::deleteNoException);
            }
        }
    }

}
