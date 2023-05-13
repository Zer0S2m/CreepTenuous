package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFileSystemObject;
import com.zer0s2m.creeptenuous.common.containers.ContainerUploadFile;
import com.zer0s2m.creeptenuous.common.http.ResponseUploadDirectoryApi;
import com.zer0s2m.creeptenuous.services.system.ServiceUnpackingDirectory;
import com.zer0s2m.creeptenuous.services.system.ServiceUploadDirectory;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ServiceFileSystem("service-upload-service")
public class ServiceUploadDirectoryImpl implements ServiceUploadDirectory, ServiceUnpackingDirectory {
    private final Logger logger = LogManager.getLogger(ServiceUploadDirectory.class);
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceUploadDirectoryImpl(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    /**
     * Run thread for unpacking zip archive
     * @param systemParents parts of the system path - target
     * @param zipFile zip archive
     * @return data upload
     * @throws IOException system error
     */
    @Async
    @Override
    public CompletableFuture<ResponseUploadDirectoryApi> upload(
            List<String> systemParents,
            MultipartFile zipFile
    ) throws IOException {
        Path path = Path.of(buildDirectoryPath.build(systemParents));
        Path newPathZipFile = Path.of(String.valueOf(path), zipFile.getOriginalFilename());
        final ContainerUploadFile container = new ContainerUploadFile();

        try {
            zipFile.transferTo(newPathZipFile);
            container.setFile(newPathZipFile);
            final List<ContainerDataUploadFileSystemObject> finalData = unpacking(container, path);
            return CompletableFuture.completedFuture(new ResponseUploadDirectoryApi(true, finalData));
        } catch (IOException | InterruptedException e) {
            logger.error(e);
            return CompletableFuture.completedFuture(new ResponseUploadDirectoryApi(false, null));
        }
    }
}