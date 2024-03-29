package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFileSystemObject;
import com.zer0s2m.creeptenuous.common.containers.ContainerUploadFile;
import com.zer0s2m.creeptenuous.common.http.ResponseUploadDirectoryApi;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.ServiceFileSystemExceptionHandlerOperationUpload;
import com.zer0s2m.creeptenuous.services.system.ServiceUploadDirectory;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

/**
 * Service for servicing directory uploads
 */
@ServiceFileSystem("service-upload-service")
@CoreServiceFileSystem(method = "upload")
public class ServiceUploadDirectoryImpl implements ServiceUploadDirectory {

    private final Logger logger = LogManager.getLogger(ServiceUploadDirectory.class);

    private final ServiceBuildDirectoryPath buildDirectoryPath = new ServiceBuildDirectoryPath();

    /**
     * Run thread for unpacking zip archive
     * @param systemPath system path from part directories
     * @param source path zip file in file system
     * @return data upload
     * @throws IOException system error
     */
    @Override
    @AtomicFileSystem(
            name = "upload-directory",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            handler = ServiceFileSystemExceptionHandlerOperationUpload.class,
                            exception = IOException.class,
                            operation = ContextAtomicFileSystem.Operations.UPLOAD
                    )
            }
    )
    public ResponseUploadDirectoryApi upload(Path systemPath, Path source) throws IOException {
        final ContainerUploadFile container = new ContainerUploadFile();

        logger.info(String.format(
                "Uploading a directory (zip archive): source [%s] output [%s]",
                source, systemPath
        ));

        try {
            container.setFile(source);
            final List<ContainerDataUploadFileSystemObject> finalData = unpacking(
                    container, systemPath, this.getClass().getCanonicalName());
            return new ResponseUploadDirectoryApi(true, finalData);
        } catch (InterruptedException e) {
            logger.error(e);
            return new ResponseUploadDirectoryApi(false, null);
        }
    }

    /**
     * Get system path from part directories
     * @param systemParents system path part directories
     * @return system path
     * @throws NoSuchFileException no file object in file system
     */
    @Override
    public Path getPath(List<String> systemParents) throws NoSuchFileException {
        return Path.of(buildDirectoryPath.build(systemParents));
    }

}