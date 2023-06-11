package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataDownloadFile;
import com.zer0s2m.creeptenuous.common.exceptions.NoSuchFileExistsException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.List;

/**
 * File download service
 */
public interface ServiceDownloadFile {

    /**
     * Get resource for download file
     * @param systemParents system path part directories
     * @param systemNameFile system name file
     * @return container data download file
     * @throws IOException error system
     * @throws NoSuchFileExistsException when no file in file system
     */
    ContainerDataDownloadFile<ByteArrayResource, String> download(
            List<String> systemParents,
            String systemNameFile
    ) throws IOException, NoSuchFileExistsException;

    /**
     * Collect headers for request
     * @param data download file information
     * @return ready-made headers for the request
     */
    HttpHeaders collectHeaders(ContainerDataDownloadFile<ByteArrayResource, String> data);

}
