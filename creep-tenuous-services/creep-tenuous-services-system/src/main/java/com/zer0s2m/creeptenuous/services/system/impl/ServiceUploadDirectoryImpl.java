package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFileSystemObject;
import com.zer0s2m.creeptenuous.common.containers.ContainerUploadFile;
import com.zer0s2m.creeptenuous.common.http.ResponseUploadDirectoryApi;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.handlers.impl.ServiceFileSystemExceptionHandlerOperationUpload;
import com.zer0s2m.creeptenuous.core.atomic.services.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.services.system.ServiceUnpackingDirectory;
import com.zer0s2m.creeptenuous.services.system.ServiceUploadDirectory;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

/**
 * Service for servicing directory uploads
 */
@ServiceFileSystem("service-upload-service")
@CoreServiceFileSystem(method = "upload")
public class ServiceUploadDirectoryImpl implements ServiceUploadDirectory, ServiceUnpackingDirectory,
        AtomicServiceFileSystem {

    private final Logger logger = LogManager.getLogger(ServiceUploadDirectory.class);

    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceUploadDirectoryImpl(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

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
      Get path source zip file in file system
     * @param path parts of the system path - target
     * @param zipFile zip archive
     * @return source zip file
     */
    public Path getNewPathZipFile(Path path, @NotNull MultipartFile zipFile) throws IOException {
        Path newPathZipFile = Path.of(String.valueOf(path), zipFile.getOriginalFilename());
        zipFile.transferTo(newPathZipFile);
        return newPathZipFile;
    }

    /**
     * Get system path from part directories
     * @param systemParents system path part directories
     * @return system path
     * @throws NoSuchFileException no file object in file system
     */
    public Path getPath(List<String> systemParents) throws NoSuchFileException {
        return Path.of(buildDirectoryPath.build(systemParents));
    }

}