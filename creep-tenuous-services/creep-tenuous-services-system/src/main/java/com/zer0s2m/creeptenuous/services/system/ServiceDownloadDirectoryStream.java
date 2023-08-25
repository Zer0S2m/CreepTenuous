package com.zer0s2m.creeptenuous.services.system;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Basic interface for providing an asynchronous response when downloading a directory
 */
public interface ServiceDownloadDirectoryStream {

    /**
     * Get an asynchronous stream for a response when downloading a directory
     * @param source source zip file
     * @return response
     */
    default StreamingResponseBody getZipFileInStream(Path source) {
        return outputStream -> Files.copy(source, outputStream);
    }

}
