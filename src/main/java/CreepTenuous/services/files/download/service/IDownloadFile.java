package CreepTenuous.services.files.download.service;

import CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import CreepTenuous.services.files.download.containers.ContainerDownloadFile3;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.List;

public interface IDownloadFile {
    ContainerDownloadFile3<ByteArrayResource, String> download(
            List<String> parents,
            String filename
    ) throws IOException, NoSuchFileExistsException;

    HttpHeaders collectHeaders(ContainerDownloadFile3<ByteArrayResource, String> data);
}
