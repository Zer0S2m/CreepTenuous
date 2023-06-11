package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.http.ResponseObjectUploadFileApi;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * File upload service
 */
public interface ServiceUploadFile {

    /**
     * Upload files
     * @param files files
     * @param systemParents parts of the system path - target
     * @return info is upload and info system file object
     * @throws IOException system error
     */
    List<ResponseObjectUploadFileApi> upload(List<MultipartFile> files, List<String> systemParents) throws IOException;

}
