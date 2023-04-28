package com.zer0s2m.CreepTenuous.api.controllers.files.upload;

import com.zer0s2m.CreepTenuous.api.controllers.files.upload.http.ResponseUploadFile;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.services.files.upload.containers.ContainerDataUploadFile;
import com.zer0s2m.CreepTenuous.services.files.upload.services.impl.ServiceUploadFile;

import com.zer0s2m.CreepTenuous.services.files.upload.services.impl.ServiceUploadFileRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@V1APIController
public class ControllerApiUploadFile implements CheckIsExistsDirectoryApi {
    private final ServiceUploadFile uploadFile;

    private final ServiceUploadFileRedis uploadFileRedis;

    @Autowired
    public ControllerApiUploadFile(ServiceUploadFile uploadFile, ServiceUploadFileRedis uploadFileRedis) {
        this.uploadFile = uploadFile;
        this.uploadFileRedis = uploadFileRedis;
    }

    @PostMapping(value = "/file/upload")
    public List<ResponseUploadFile> upload(
            final @RequestPart("files") List<MultipartFile> files,
            final @RequestParam("parents") List<String> parents,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws IOException {
        List<ResponseUploadFile> data = uploadFile.upload(files, parents);
        uploadFileRedis.setAccessToken(accessToken);
        uploadFileRedis.create(data
                .stream()
                .map((obj) -> new ContainerDataUploadFile(obj.fileName(), obj.path()))
                .collect(Collectors.toList()));
        return data;
    }
}
