package CreepTenuous.api.controllers.files.downloadFile;

import CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import CreepTenuous.api.controllers.files.downloadFile.data.DataDownloadFile;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.services.directory.utils.check.CheckIsExistsDirectoryApi;
import CreepTenuous.services.files.downloadFile.service.containers.ContainerDownloadFile3;
import CreepTenuous.services.files.downloadFile.service.impl.DownloadFile;
import CreepTenuous.services.files.utils.check.CheckIsExistsFileApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@V1APIController
public class DownloadFileApi implements CheckIsExistsDirectoryApi, CheckIsExistsFileApi {
    @Autowired
    private DownloadFile downloadFile;

    @GetMapping(path = "/file/download")
    public ResponseEntity<Resource> download(
            final DataDownloadFile data
    ) throws IOException, NoSuchFileExistsException {
        final ContainerDownloadFile3<ByteArrayResource, String> dataFile = downloadFile.download(
                data.getParents(),
                data.getFilename()
        );
        return ResponseEntity.ok()
                .headers(downloadFile.collectHeaders(dataFile))
                .body(dataFile.getByteContent());
    }
}
