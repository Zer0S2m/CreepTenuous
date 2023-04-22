package com.zer0s2m.CreepTenuous.services.directory.upload.services.impl;

import com.zer0s2m.CreepTenuous.api.controllers.directory.upload.http.ResponseUploadDirectory;
import com.zer0s2m.CreepTenuous.services.directory.upload.containers.ContainerUploadFile;
import com.zer0s2m.CreepTenuous.services.directory.upload.services.IUploadDirectory;
import com.zer0s2m.CreepTenuous.services.directory.upload.services.IUnpackingDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service("upload-service")
public class ServiceUploadDirectory implements IUploadDirectory, IUnpackingDirectory {
    private final Logger logger = LogManager.getLogger(ServiceUploadDirectory.class);
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceUploadDirectory(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    public ResponseUploadDirectory upload(
            List<String> parents,
            MultipartFile zipFile
    ) throws IOException {
        Path path = Path.of(buildDirectoryPath.build(parents));
        final ContainerUploadFile container = new ContainerUploadFile();

        Path newPathZipFile = Path.of(String.valueOf(path), zipFile.getOriginalFilename());
        try {
            zipFile.transferTo(newPathZipFile);
            container.setFile(newPathZipFile);
            unpacking(container, path);
            return new ResponseUploadDirectory(true);
        } catch (IOException e) {
            logger.error(e);
            return new ResponseUploadDirectory(false);
        }
    }
}
