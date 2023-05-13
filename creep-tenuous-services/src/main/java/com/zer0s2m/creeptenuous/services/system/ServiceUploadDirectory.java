package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.http.ResponseUploadDirectoryApi;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ServiceUploadDirectory {
    /**
     * Run thread for unpacking zip archive
     * @param systemParents parts of the system path - target
     * @param zipFile zip archive
     * @return data upload
     * @throws IOException system error
     */
    CompletableFuture<ResponseUploadDirectoryApi> upload(List<String> systemParents, MultipartFile zipFile)
            throws IOException;
}
