package com.zer0s2m.CreepTenuous.api.controllers.files.upload;

import com.zer0s2m.CreepTenuous.api.controllers.files.upload.http.DataUploadFile;
import com.zer0s2m.CreepTenuous.api.controllers.files.upload.http.ResponseUploadFile;
import com.zer0s2m.CreepTenuous.api.core.annotations.V1APIRestController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.providers.redis.controllers.CheckRightsActionFileSystem;
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

@V1APIRestController
public class ControllerApiUploadFile implements CheckIsExistsDirectoryApi, CheckRightsActionFileSystem {
    private final ServiceUploadFile uploadFile;

    private final ServiceUploadFileRedis uploadFileRedis;

    @Autowired
    public ControllerApiUploadFile(ServiceUploadFile uploadFile, ServiceUploadFileRedis uploadFileRedis) {
        this.uploadFile = uploadFile;
        this.uploadFileRedis = uploadFileRedis;
    }

    @PostMapping(value = "/file/upload")
    public ResponseUploadFile upload(
            final @RequestPart("files") List<MultipartFile> files,
            final @RequestParam("parents") List<String> parents,
            final @RequestParam("systemParents") List<String> systemParents,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws IOException {
        uploadFileRedis.setAccessToken(accessToken);
        uploadFileRedis.checkRights(parents, systemParents, null);

        List<DataUploadFile> data = uploadFile.upload(files, systemParents);
        uploadFileRedis.create(data
                .stream()
                .map((obj) -> {
                    if (obj.success()) {
                        return new ContainerDataUploadFile(
                                obj.realFileName(),
                                obj.systemFileName(),
                                obj.realPath(),
                                obj.systemPath()
                        );
                    }
                    return null;
                })
                .collect(Collectors.toList()));
        return new ResponseUploadFile(data);
    }
}
