package com.zer0s2m.CreepTenuous.api.controllers.directory.upload;

import com.zer0s2m.CreepTenuous.api.controllers.directory.upload.http.ResponseUploadDirectory;
import com.zer0s2m.CreepTenuous.api.core.annotations.V1APIRestController;
import com.zer0s2m.CreepTenuous.services.directory.upload.services.impl.ServiceUploadDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@V1APIRestController
public class ControllerApiUploadDirectory implements CheckIsExistsDirectoryApi {
    private final ServiceUploadDirectory uploadDirectory;

    @Autowired
    public ControllerApiUploadDirectory(ServiceUploadDirectory uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }

    @PostMapping(path = "/directory/upload")
    @ResponseStatus(code = HttpStatus.CREATED)
    public final ResponseUploadDirectory upload(
            final @Nullable @RequestParam("parents") List<String> parents,
            final @RequestPart("directory") MultipartFile zipFile
    ) throws IOException {
        return uploadDirectory.upload(parents, zipFile);
    }
}
