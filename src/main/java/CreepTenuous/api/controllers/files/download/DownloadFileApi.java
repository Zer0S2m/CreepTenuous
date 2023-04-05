package CreepTenuous.api.controllers.files.download;

import CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import CreepTenuous.api.controllers.files.download.data.DataDownloadFile;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import CreepTenuous.services.files.download.containers.ContainerDownloadFile3;
import CreepTenuous.services.files.download.services.impl.DownloadFile;
import CreepTenuous.providers.build.os.services.CheckIsExistsFileApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

import java.io.IOException;

@V1APIController
public class DownloadFileApi implements CheckIsExistsDirectoryApi, CheckIsExistsFileApi {
    private final DownloadFile downloadFile;

    @Autowired
    public DownloadFileApi(DownloadFile downloadFile) {
        this.downloadFile = downloadFile;
    }

    @GetMapping(path = "/file/download")
    public Mono<ResponseEntity<Resource>> download(
            final DataDownloadFile data
    ) throws IOException, NoSuchFileExistsException {
        final ContainerDownloadFile3<ByteArrayResource, String> dataFile = downloadFile.download(
                data.parents(),
                data.filename()
        );
        return Mono.just(ResponseEntity.ok()
                .headers(downloadFile.collectHeaders(dataFile))
                .body(dataFile.byteContent())
        );
    }
}
