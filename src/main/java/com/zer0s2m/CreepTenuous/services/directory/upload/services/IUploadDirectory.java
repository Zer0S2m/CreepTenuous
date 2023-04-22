package com.zer0s2m.CreepTenuous.services.directory.upload.services;

import com.zer0s2m.CreepTenuous.api.controllers.directory.upload.http.ResponseUploadDirectory;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IUploadDirectory {
    ResponseUploadDirectory upload(List<String> parents, MultipartFile zipFile) throws IOException;
}
