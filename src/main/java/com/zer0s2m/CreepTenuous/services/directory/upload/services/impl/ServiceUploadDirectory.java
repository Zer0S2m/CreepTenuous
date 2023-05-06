package com.zer0s2m.CreepTenuous.services.directory.upload.services.impl;

import com.zer0s2m.CreepTenuous.api.controllers.directory.upload.http.ResponseUploadDirectory;
import com.zer0s2m.CreepTenuous.services.core.ServiceFileSystem;
import com.zer0s2m.CreepTenuous.services.directory.upload.containers.ContainerDataUploadFile;
import com.zer0s2m.CreepTenuous.services.directory.upload.containers.ContainerUploadFile;
import com.zer0s2m.CreepTenuous.services.directory.upload.services.IServiceUploadDirectory;
import com.zer0s2m.CreepTenuous.services.directory.upload.services.IServiceUnpackingDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ServiceFileSystem("upload-service")
public class ServiceUploadDirectory implements IServiceUploadDirectory, IServiceUnpackingDirectory {
    private final Logger logger = LogManager.getLogger(ServiceUploadDirectory.class);
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceUploadDirectory(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    @Async
    @Override
    public CompletableFuture<ResponseUploadDirectory> upload(
            List<String> parents,
            MultipartFile zipFile
    ) throws IOException {
        Path path = Path.of(buildDirectoryPath.build(parents));
        Path newPathZipFile = Path.of(String.valueOf(path), zipFile.getOriginalFilename());
        final ContainerUploadFile container = new ContainerUploadFile();

        try {
            zipFile.transferTo(newPathZipFile);
            container.setFile(newPathZipFile);
            final List<ContainerDataUploadFile> finalData = unpacking(container, path);
            return CompletableFuture.completedFuture(new ResponseUploadDirectory(true, finalData));
        } catch (IOException | InterruptedException e) {
            logger.error(e);
            return CompletableFuture.completedFuture(new ResponseUploadDirectory(false, null));
        }
    }
}
