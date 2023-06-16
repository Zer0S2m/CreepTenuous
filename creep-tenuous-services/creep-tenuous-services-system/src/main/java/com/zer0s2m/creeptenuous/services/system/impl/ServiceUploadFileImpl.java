package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.http.ResponseObjectUploadFileApi;
import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.context.nio.file.FilesContextAtomic;
import com.zer0s2m.creeptenuous.core.handlers.impl.ServiceFileSystemExceptionHandlerOperationUpload;
import com.zer0s2m.creeptenuous.core.services.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.services.system.ServiceUploadFile;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.utils.UtilsFiles;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * File upload service
 */
@ServiceFileSystem("service-upload-file")
@CoreServiceFileSystem(method = "upload")
public class ServiceUploadFileImpl implements ServiceUploadFile, AtomicServiceFileSystem {

    Logger logger = LogManager.getLogger(ServiceUploadFileImpl.class);

    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceUploadFileImpl(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    /**
     * Upload files
     * @param files files
     * @param systemParents parts of the system path - target
     * @return info is upload and info system file object
     * @throws IOException system error
     */
    @Override
    @AtomicFileSystem(
            name = "upload-file",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            handler = ServiceFileSystemExceptionHandlerOperationUpload.class,
                            exception = IOException.class,
                            operation = ContextAtomicFileSystem.Operations.UPLOAD
                    )
            }
    )
    public List<ResponseObjectUploadFileApi> upload(@NotNull List<MultipartFile> files, List<String> systemParents)
            throws IOException {
        Path path = Paths.get(buildDirectoryPath.build(systemParents));
        return files
                .stream()
                .map((file) -> {
                    String originFileName = file.getOriginalFilename();

                    String fileName = StringUtils.cleanPath(Objects.requireNonNull(originFileName));
                    String newFileName = UtilsFiles.getNewFileName(originFileName);
                    Path targetLocation = path.resolve(fileName);
                    Path newTargetLocation = path.resolve(newFileName);

                    try {
                        FilesContextAtomic.copy(
                                file.getInputStream(),
                                newTargetLocation,
                                StandardCopyOption.REPLACE_EXISTING
                        );
                    } catch (IOException e) {
                        logger.error(e);
                        return new ResponseObjectUploadFileApi(
                                fileName,
                                newFileName,
                                false,
                                targetLocation,
                                newTargetLocation
                        );
                    }
                    return new ResponseObjectUploadFileApi(
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
