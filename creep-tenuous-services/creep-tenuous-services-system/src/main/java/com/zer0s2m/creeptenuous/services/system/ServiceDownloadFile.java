package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataDownloadFile;
import com.zer0s2m.creeptenuous.core.balancer.exceptions.FileIsDirectoryException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;

/**
 * Interface for implementing a class that allows you to load regular
 * files and also files that are fragmented.
 */
public interface ServiceDownloadFile {

    /**
     * Get resource for download file.
     *
     * @param systemParents  System names of directories from which the directory path will be
     *                       collected where the file.
     * @param systemNameFile System name file.
     * @return Container data download file.
     * @throws IOException               signals that an I/O exception to some sort has occurred.
     */
    ContainerDataDownloadFile<StreamingResponseBody, String> download(
            List<String> systemParents, String systemNameFile) throws IOException;

    /**
     * Get the fragmented file to download.
     *
     * @param systemParents  System names of directories from which the directory path will be
     *                       collected where the file.
     * @param systemNameFile System name file.
     * @return Container data download file.
     * @throws IOException              signals that an I/O exception to some sort has occurred.
     * @throws FileIsDirectoryException The exception indicates that the source file object is a directory.
     */
    ContainerDataDownloadFile<StreamingResponseBody, String> downloadFragment(
            List<String> systemParents, String systemNameFile) throws IOException, FileIsDirectoryException;

    /**
     * Collect headers for request.
     *
     * @param data Download file information.
     * @return Ready-made headers for the request.
     */
    default HttpHeaders collectHeaders(
            @NotNull ContainerDataDownloadFile<StreamingResponseBody, String> data) {
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
