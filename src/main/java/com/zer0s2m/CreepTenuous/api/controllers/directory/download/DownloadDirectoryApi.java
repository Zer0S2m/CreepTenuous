package com.zer0s2m.CreepTenuous.api.controllers.directory.download;

import com.zer0s2m.CreepTenuous.api.controllers.directory.download.data.DataDownloadDirectory;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.services.directory.download.services.impl.DownloadDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

import java.io.IOException;

@V1APIController
public class DownloadDirectoryApi implements CheckIsExistsDirectoryApi {
    private final DownloadDirectory downloadDirectory;

    @Autowired
    public DownloadDirectoryApi(DownloadDirectory downloadDirectory) {
        this.downloadDirectory = downloadDirectory;
    }

    @GetMapping(path = "/directory/download")
    public final Mono<ResponseEntity<Resource>> download(
            final DataDownloadDirectory data
    ) throws IOException {
        return downloadDirectory.download(data.parents(), data.directory());
    }
}
