package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataDownloadFile;
import com.zer0s2m.creeptenuous.services.system.ServiceDownloadFile;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * File download service
 */
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
     *
     * @param systemParents  system path part directories
     * @param systemNameFile system name file
     * @return container data download file
     * @throws IOException error system
     */
    @Override
    public ContainerDataDownloadFile<StreamingResponseBody, String> download(
            List<String> systemParents,
            String systemNameFile
    ) throws IOException {
        Path pathFile = Paths.get(buildDirectoryPath.build(systemParents), systemNameFile);
        if (!Files.exists(pathFile)) {
            throw new NoSuchFileException(pathFile.getFileName().toString());
        }

        StreamingResponseBody responseBody = outputStream -> Files.copy(pathFile, outputStream);

        return new ContainerDataDownloadFile<>(
                responseBody,
                fileTypeMap.getContentType(pathFile.toString()),
                systemNameFile
        );
    }

    /**
     * Collect headers for request
     * @param data download file information
     * @return ready-made headers for the request
     */
    @Override
    public HttpHeaders collectHeaders(@NotNull ContainerDataDownloadFile<StreamingResponseBody, String> data) {
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

        return headers;
    }

}
