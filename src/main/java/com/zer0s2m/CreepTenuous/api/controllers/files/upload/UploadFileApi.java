package com.zer0s2m.CreepTenuous.api.controllers.files.upload;

import com.zer0s2m.CreepTenuous.api.controllers.files.upload.http.ResponseUploadFile;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.services.files.upload.services.impl.UploadFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@V1APIController
public class UploadFileApi implements CheckIsExistsDirectoryApi {
    private final UploadFile uploadFile;

    @Autowired
    public UploadFileApi(UploadFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    @PostMapping(value = "/file/upload")
    public Mono<ResponseUploadFile> upload(
            final @RequestPart("files") Flux<FilePart> files,
            final @RequestParam("parents") List<String> parents
    ) throws IOException {
        return uploadFile.upload(files, parents);
    }
}