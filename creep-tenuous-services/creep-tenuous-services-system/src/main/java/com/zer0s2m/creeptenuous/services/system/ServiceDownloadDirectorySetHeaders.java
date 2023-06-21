package com.zer0s2m.creeptenuous.services.system;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;

import java.nio.file.Path;

/**
 * Interface for setting http headers for serving zip archives
 */
public interface ServiceDownloadDirectorySetHeaders {

    /**
     * Collect headers for download directory
     * @param path source archive zip
     * @param data byre data
     * @return headers
     */
    HttpHeaders collectHeaders(Path path, ByteArrayResource data);

}
