package com.zer0s2m.CreepTenuous.services.directory.upload.services;

import com.zer0s2m.CreepTenuous.api.controllers.directory.upload.http.ResponseUploadDirectory;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IServiceUploadDirectory {
    /**
     * Run thread for unpacking zip archive
     * @param systemParents parts of the system path - target
     * @param zipFile zip archive
     * @return data upload
     * @throws IOException system error
     */
    CompletableFuture<ResponseUploadDirectory> upload(List<String> systemParents, MultipartFile zipFile)
            throws IOException;
}
