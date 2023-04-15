package com.zer0s2m.CreepTenuous.api.controllers.files.upload;

import com.zer0s2m.CreepTenuous.api.controllers.files.upload.http.ResponseUploadFile;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.services.files.upload.services.impl.ServiceUploadFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@V1APIController
public class ControllerApiUploadFile implements CheckIsExistsDirectoryApi {
    private final ServiceUploadFile uploadFile;

    @Autowired
    public ControllerApiUploadFile(ServiceUploadFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    @PostMapping(value = "/file/upload")
    public List<ResponseUploadFile> upload(
            final @RequestPart("files") List<MultipartFile> files,
            final @RequestParam("parents") List<String> parents
    ) throws IOException {
        return uploadFile.upload(files, parents);
    }
}
