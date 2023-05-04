package com.zer0s2m.CreepTenuous.api.controllers.directory.upload;

import com.zer0s2m.CreepTenuous.api.controllers.directory.upload.http.ResponseUploadDirectory;
import com.zer0s2m.CreepTenuous.api.core.annotations.V1APIRestController;
import com.zer0s2m.CreepTenuous.services.directory.upload.services.impl.ServiceUploadDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.services.directory.upload.services.impl.ServiceUploadDirectoryRedis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@EnableAsync
@V1APIRestController
public class ControllerApiUploadDirectory implements CheckIsExistsDirectoryApi {
    private final ServiceUploadDirectory serviceUploadDirectory;

    private final ServiceUploadDirectoryRedis serviceUploadDirectoryRedis;

    @Autowired
    public ControllerApiUploadDirectory(
            ServiceUploadDirectory serviceUploadDirectory,
            ServiceUploadDirectoryRedis serviceUploadDirectoryRedis
    ) {
        this.serviceUploadDirectory = serviceUploadDirectory;
        this.serviceUploadDirectoryRedis = serviceUploadDirectoryRedis;
    }

    @PostMapping(path = "/directory/upload")
    @ResponseStatus(code = HttpStatus.CREATED)
    public final ResponseUploadDirectory upload(
            final @Nullable @RequestParam("parents") List<String> parents,
            final @Nullable @RequestParam("systemParents") List<String> systemParents,
            final @RequestPart("directory") MultipartFile zipFile,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws IOException, ExecutionException, InterruptedException {
        serviceUploadDirectoryRedis.setAccessToken(accessToken);
        serviceUploadDirectoryRedis.checkRights(parents, systemParents, null);
        final ResponseUploadDirectory finalData = serviceUploadDirectory.upload(systemParents, zipFile).get();
        serviceUploadDirectoryRedis.upload(finalData.data());
        return finalData;
    }
}
