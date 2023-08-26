package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.services.system.ServiceDownloadDirectorySetHeaders;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;

import java.nio.file.Path;

/**
 * Service for setting http headers for serving zip archives
 */
public class ServiceDownloadDirectorySetHeadersImpl implements ServiceDownloadDirectorySetHeaders {

    /**
     * Collect headers for download directory
     * @param path source archive zip
     * @return headers
     */
    @Override
    public HttpHeaders collectHeaders(@NotNull Path path) {
        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.EXPIRES, "1");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition
                .attachment()
                .filename(String.valueOf(path.getFileName()))
                .build()
                .toString()
        );
        headers.add(HttpHeaders.CONTENT_TYPE, Directory.TYPE_APPLICATION_ZIP.get());

        return headers;
    }

}
