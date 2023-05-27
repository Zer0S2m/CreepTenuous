package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.context.nio.file.FilesContextAtomic;
import com.zer0s2m.creeptenuous.core.handlers.impl.ServiceFileSystemExceptionHandlerOperationDelete;
import com.zer0s2m.creeptenuous.core.services.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.services.system.ServiceDeleteFile;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
}
