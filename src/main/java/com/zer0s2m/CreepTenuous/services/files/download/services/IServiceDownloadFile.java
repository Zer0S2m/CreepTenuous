package com.zer0s2m.CreepTenuous.services.files.download.services;

import com.zer0s2m.CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import com.zer0s2m.CreepTenuous.services.files.download.containers.ContainerDownloadFile3;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.List;

public interface IServiceDownloadFile {
    /**
     * Get resource for download file
     * @param systemParents system path part directories
     * @param systemNameFile system name file
     * @return container data download file
     * @throws IOException error system
     * @throws NoSuchFileExistsException when no file in file system
     */
    ContainerDownloadFile3<ByteArrayResource, String> download(
            List<String> systemParents,
            String systemNameFile
    ) throws IOException, NoSuchFileExistsException;

    HttpHeaders collectHeaders(ContainerDownloadFile3<ByteArrayResource, String> data);
}
