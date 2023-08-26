package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.context.nio.file.FilesContextAtomic;
import com.zer0s2m.creeptenuous.core.atomic.handlers.impl.ServiceFileSystemExceptionHandlerOperationDelete;
import com.zer0s2m.creeptenuous.core.atomic.services.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.services.system.ServiceDeleteFile;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Maintenance service file deletion
 */
@ServiceFileSystem("service-delete-file")
@CoreServiceFileSystem(method = "delete")
public class ServiceDeleteFileImpl implements ServiceDeleteFile, AtomicServiceFileSystem {

    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceDeleteFileImpl(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    /**
     * Delete file from file system
     * @param systemNameFile system name file
     * @param systemParents system path part directories
     * @return source path system
     * @throws IOException error system
     */
    @Override
    @AtomicFileSystem(
            name = "delete-file",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            exception = IOException.class,
                            handler = ServiceFileSystemExceptionHandlerOperationDelete.class,
                            operation = ContextAtomicFileSystem.Operations.DELETE
                    )
            }
    )
    public Path delete(String systemNameFile, List<String> systemParents) throws IOException {
        Path pathFile = Paths.get(buildDirectoryPath.build(systemParents), systemNameFile);
        FilesContextAtomic.delete(pathFile);
        return pathFile;
    }

    /**
     * Delete file from file system
     * @param source source path system
     * @return source path system
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     */
    @Override
    @AtomicFileSystem(
            name = "delete-file",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            exceptionMulti = IOException.class,
                            isExceptionMulti = true,
                            handler = ServiceFileSystemExceptionHandlerOperationDelete.class,
                            operation = ContextAtomicFileSystem.Operations.DELETE
                    )
            }
    )
    public Path delete(Path source) throws IOException {
        FilesContextAtomic.delete(source);
        return source;
    }

}
