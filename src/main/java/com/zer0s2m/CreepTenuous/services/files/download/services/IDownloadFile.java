package com.zer0s2m.CreepTenuous.services.files.download.services;

import com.zer0s2m.CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import com.zer0s2m.CreepTenuous.services.files.download.containers.ContainerDownloadFile3;

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
