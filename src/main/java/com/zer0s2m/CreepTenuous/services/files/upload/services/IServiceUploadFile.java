package com.zer0s2m.CreepTenuous.services.files.upload.services;

import com.zer0s2m.CreepTenuous.api.controllers.files.upload.http.DataUploadFile;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IServiceUploadFile {
    /**
     * Upload files
     * @param files files
     * @param systemParents parts of the system path - target
     * @return info is upload and info system file object
     * @throws IOException system error
     */
    List<DataUploadFile> upload(List<MultipartFile> files, List<String> systemParents) throws IOException;
}
