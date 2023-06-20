package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectorySelectPartApi;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Interface for downloading a directory as a selection of file objects
 */
public interface ServiceDownloadDirectorySelect {

    /**
     * Download selectively file objects
     * @param data object file system information
     * @return archive zip
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     */
    ResponseEntity<Resource> download(List<DataDownloadDirectorySelectPartApi> data) throws IOException;

    /**
     * Set resource for archiving directory
     * @param map data
     */
    void setMap(HashMap<String, String> map);

}
