package com.zer0s2m.CreepTenuous.services.files.download.services.impl;

import com.zer0s2m.CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.core.ServiceFileSystem;
import com.zer0s2m.CreepTenuous.services.files.download.services.IDownloadFile;
import com.zer0s2m.CreepTenuous.services.files.download.containers.ContainerDownloadFile3;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsFileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@ServiceFileSystem("download-file")
public class ServiceDownloadFile implements IDownloadFile, CheckIsExistsFileService {
    private final ServiceBuildDirectoryPath buildDirectoryPath;
    private final ConfigurableMimeFileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();

    @Autowired
    public ServiceDownloadFile(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    /**
     * Get resource for download file
     * @param systemParents system path part directories
     * @param systemNameFile system name file
     * @return container data download file
     * @throws IOException error system
     * @throws NoSuchFileExistsException when no file in file system
     */
    @Override
    public ContainerDownloadFile3<ByteArrayResource, String> download(
            List<String> systemParents,
            String systemNameFile
    ) throws IOException, NoSuchFileExistsException {
        Path pathFile = Paths.get(buildDirectoryPath.build(systemParents), systemNameFile);
        checkFile(pathFile);

        return new ContainerDownloadFile3<>(
                new ByteArrayResource(Files.readAllBytes(pathFile)),
                fileTypeMap.getContentType(pathFile.toString()),
                systemNameFile
        );
    }

    @Override
    public HttpHeaders collectHeaders(ContainerDownloadFile3<ByteArrayResource, String> data) {
        final HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition
                .attachment()
                .filename(data.filename())
                .build()
                .toString()
        );
        headers.add(HttpHeaders.CONTENT_TYPE, data.mimeType());
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.byteContent().contentLength()));

        return headers;
    }
}
