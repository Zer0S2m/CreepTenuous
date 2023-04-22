package com.zer0s2m.CreepTenuous.services.files.upload.services;

import com.zer0s2m.CreepTenuous.api.controllers.files.upload.http.ResponseUploadFile;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IUploadFile {
    List<ResponseUploadFile> upload(List<MultipartFile> files, List<String> parents) throws IOException;
}
