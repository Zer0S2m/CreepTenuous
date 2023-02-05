package CreepTenuous.api.controllers.directory.download;

import CreepTenuous.api.controllers.directory.download.data.DataDownloadDirectory;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.services.directory.download.impl.DownloadDirectory;
import CreepTenuous.services.directory.utils.check.CheckIsExistsDirectoryApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

import java.io.IOException;

@V1APIController
public class DownloadDirectoryApi implements CheckIsExistsDirectoryApi {
    @Autowired
    private DownloadDirectory downloadDirectory;

    @GetMapping(path = "/directory/download")
    public final Mono<ResponseEntity<Resource>> download(
            final DataDownloadDirectory data
    ) throws IOException {
        return downloadDirectory.download(data.getParents(), data.getDirectory());
    }
}
