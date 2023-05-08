package com.zer0s2m.CreepTenuous.services.directory.download.services.impl;

import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceDownloadDirectoryRedis;
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
import java.util.HashMap;
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

    /**
     * info directory (get data - {@link IServiceDownloadDirectoryRedis#getResource(List)})
     */
    private HashMap<String, String> map = null;

    @Autowired
    public ServiceDownloadDirectory(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    /**
     * Download directory archive zip
     * @param systemParents parts of the system path - source
     * @param systemNameDirectory system name directory
     * @return archive zip
     * @throws IOException system error
     */
    @Override
    public ResponseEntity<Resource> download(
            List<String> systemParents,
            String systemNameDirectory
    ) throws IOException {
        Path source = Paths.get(buildDirectoryPath.build(systemParents), systemNameDirectory);
        checkDirectory(source);

        Path pathToZip = collectZip(source, this.map);
        ByteArrayResource contentBytes = new ByteArrayResource(Files.readAllBytes(pathToZip));

        deleteFileZip(pathToZip);
        return ResponseEntity.ok()
                .headers(collectHeaders(pathToZip, contentBytes))
                .body(contentBytes);
    }

    /**
     * Collect headers for download directory
     * @param path source archive zip
     * @param data byre data
     * @return headers
     */
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

    /**
     * Delete zip archive
     * @param source source zip archive
     */
    private void deleteFileZip(Path source) {
        boolean isDeleted = source.toFile().delete();
        logger.info("Is deleted zip file: " + isDeleted);
    }

    /**
     * Set resource for archiving directory
     * @param map data
     */
    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }
}
