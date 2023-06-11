package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.handlers.impl.ServiceFileSystemExceptionHandlerOperationDownload;
import com.zer0s2m.creeptenuous.core.services.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDownloadDirectoryRedis;
import com.zer0s2m.creeptenuous.services.system.CollectZipDirectory;
import com.zer0s2m.creeptenuous.services.system.ServiceDownloadDirectory;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.nio.file.Path;

/**
 * Service for servicing the download of a catalog in a zip archive
 */
@ServiceFileSystem("service-download-directory")
@CoreServiceFileSystem(method = "download")
public class ServiceDownloadDirectoryImpl
        implements ServiceDownloadDirectory, CollectZipDirectory, AtomicServiceFileSystem {

    private final Logger logger = LogManager.getLogger(ServiceDownloadDirectoryImpl.class);

    private final ServiceBuildDirectoryPath buildDirectoryPath;

    /**
     * info directory (get data - {@link ServiceDownloadDirectoryRedis#getResource(List)})
     */
    private HashMap<String, String> map = null;

    @Autowired
    public ServiceDownloadDirectoryImpl(ServiceBuildDirectoryPath buildDirectoryPath) {
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
    @AtomicFileSystem(
            name = "delete-file",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            exception = IOException.class,
                            handler = ServiceFileSystemExceptionHandlerOperationDownload.class,
                            operation = ContextAtomicFileSystem.Operations.DOWNLOAD
                    )
            }
    )
    public ResponseEntity<Resource> download(
            List<String> systemParents,
            String systemNameDirectory
    ) throws IOException {
        Path source = Paths.get(buildDirectoryPath.build(systemParents), systemNameDirectory);

        Path pathToZip = collectZip(source, this.map, this.getClass().getCanonicalName());
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
    public HttpHeaders collectHeaders(@NotNull Path path, @NotNull ByteArrayResource data) {
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
    private void deleteFileZip(@NotNull Path source) {
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
