package com.zer0s2m.CreepTenuous.services.directory.download.services.impl;

import com.zer0s2m.CreepTenuous.services.core.Directory;
import com.zer0s2m.CreepTenuous.services.directory.download.services.IDownloadDirectory;
import com.zer0s2m.CreepTenuous.services.directory.download.services.ICollectZipDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.nio.file.Path;

@Service("download-directory")
public class ServiceDownloadDirectory implements
        IDownloadDirectory,
        ICollectZipDirectory,
        CheckIsExistsDirectoryService
{
    private final Logger logger = LogManager.getLogger(ServiceDownloadDirectory.class);
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceDownloadDirectory(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    @Override
    public ResponseEntity<Resource> download(
            List<String> parents,
            String directory
    ) throws IOException {
        Path path = Paths.get(buildDirectoryPath.build(parents), directory);
        checkDirectory(path);

        Path pathToZip = collectZip(path);
        ByteArrayResource contentBytes = new ByteArrayResource(Files.readAllBytes(pathToZip));

        deleteFileZip(pathToZip);
        return ResponseEntity.ok()
                .headers(collectHeaders(pathToZip, contentBytes))
                .body(contentBytes);
    }

    @Override
    public HttpHeaders collectHeaders(Path path, ByteArrayResource data) {
        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.EXPIRES, "1");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition
                .attachment()
                .filename(String.valueOf(path.getFileName()))
                .build()
                .toString()
        );
        headers.add(HttpHeaders.CONTENT_TYPE, Directory.TYPE_APPLICATION_ZIP.get());
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.contentLength()));

        return headers;
    }

    private void deleteFileZip(Path path) {
        boolean isDeleted = path.toFile().delete();
        logger.info("Is deleted zip file: " + isDeleted);
    }
}
