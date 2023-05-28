package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataDownloadFile;
import com.zer0s2m.creeptenuous.services.system.ServiceDownloadFile;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
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

@ServiceFileSystem("service-download-file")
public class ServiceDownloadFileImpl implements ServiceDownloadFile {
    private final ServiceBuildDirectoryPath buildDirectoryPath;
    private final ConfigurableMimeFileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();

    @Autowired
    public ServiceDownloadFileImpl(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    /**
     * Get resource for download file
     * @param systemParents system path part directories
     * @param systemNameFile system name file
     * @return container data download file
     * @throws IOException error system
     */
    @Override
    public ContainerDataDownloadFile<ByteArrayResource, String> download(
            List<String> systemParents,
            String systemNameFile
    ) throws IOException {
        Path pathFile = Paths.get(buildDirectoryPath.build(systemParents), systemNameFile);

        return new ContainerDataDownloadFile<>(
                new ByteArrayResource(Files.readAllBytes(pathFile)),
                fileTypeMap.getContentType(pathFile.toString()),
                systemNameFile
        );
    }

    @Override
    public HttpHeaders collectHeaders(ContainerDataDownloadFile<ByteArrayResource, String> data) {
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
