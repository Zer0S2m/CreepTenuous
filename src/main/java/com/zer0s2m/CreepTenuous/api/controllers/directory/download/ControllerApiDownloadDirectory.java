package com.zer0s2m.CreepTenuous.api.controllers.directory.download;

import com.zer0s2m.CreepTenuous.api.controllers.directory.download.data.DataDownloadDirectory;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.services.directory.download.services.impl.ServiceDownloadDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@V1APIController
public class ControllerApiDownloadDirectory implements CheckIsExistsDirectoryApi {
    private final ServiceDownloadDirectory downloadDirectory;

    @Autowired
    public ControllerApiDownloadDirectory(ServiceDownloadDirectory downloadDirectory) {
        this.downloadDirectory = downloadDirectory;
    }

    @GetMapping(path = "/directory/download")
    public final ResponseEntity<Resource> download(final @Valid DataDownloadDirectory data) throws IOException {
        return downloadDirectory.download(data.parents(), data.directory());
    }
}
