package com.zer0s2m.CreepTenuous.services.files.upload.services.impl;

import com.zer0s2m.CreepTenuous.api.controllers.files.upload.http.ResponseUploadFile;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.core.ServiceFileSystem;
import com.zer0s2m.CreepTenuous.services.files.upload.services.IUploadFile;
import com.zer0s2m.CreepTenuous.utils.UtilsFiles;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ServiceFileSystem("upload-file")
public class ServiceUploadFile implements IUploadFile {
    Logger logger = LogManager.getLogger(ServiceUploadFile.class);

    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceUploadFile(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    @Override
    public List<ResponseUploadFile> upload(
            List<MultipartFile> files,
            List<String> parents
    ) throws IOException {
        Path path = Paths.get(buildDirectoryPath.build(parents));
        return files
                .stream()
                .map((file) -> {
                    String originFileName = file.getOriginalFilename();

                    String fileName = StringUtils.cleanPath(Objects.requireNonNull(originFileName));
                    String newFileName = UtilsFiles.getNewFileName(originFileName);
                    Path targetLocation = path.resolve(fileName);
                    Path newTargetLocation = path.resolve(newFileName);

                    try {
                        Files.copy(file.getInputStream(), newTargetLocation, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        logger.error(e);
                        return new ResponseUploadFile(
                                fileName,
                                newFileName,
                                false,
                                targetLocation,
                                newTargetLocation
                        );
                    }
                    return new ResponseUploadFile(
                            fileName,
                            newFileName,
                            true,
                            targetLocation,
                            newTargetLocation
                    );
                })
                .collect(Collectors.toList());
    }
}
