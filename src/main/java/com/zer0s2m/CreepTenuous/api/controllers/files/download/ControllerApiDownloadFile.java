package com.zer0s2m.CreepTenuous.api.controllers.files.download;

import com.zer0s2m.CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import com.zer0s2m.CreepTenuous.api.controllers.files.download.data.DataDownloadFile;
import com.zer0s2m.CreepTenuous.api.core.annotations.V1APIRestController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.services.files.download.containers.ContainerDownloadFile3;
import com.zer0s2m.CreepTenuous.services.files.download.services.impl.ServiceDownloadFile;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsFileApi;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@V1APIRestController
public class ControllerApiDownloadFile implements CheckIsExistsDirectoryApi, CheckIsExistsFileApi {
    private final ServiceDownloadFile downloadFile;

    @Autowired
    public ControllerApiDownloadFile(ServiceDownloadFile downloadFile) {
        this.downloadFile = downloadFile;
    }

    @GetMapping(path = "/file/download")
    public ResponseEntity<Resource> download(
            final @Valid DataDownloadFile data
    ) throws IOException, NoSuchFileExistsException {
        final ContainerDownloadFile3<ByteArrayResource, String> dataFile = downloadFile.download(
                data.parents(),
                data.filename()
        );
        return ResponseEntity.ok()
                .headers(downloadFile.collectHeaders(dataFile))
                .body(dataFile.byteContent()
        );
    }
}
