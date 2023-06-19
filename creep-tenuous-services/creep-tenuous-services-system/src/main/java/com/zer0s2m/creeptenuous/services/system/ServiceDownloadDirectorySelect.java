package com.zer0s2m.creeptenuous.services.system;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

/**
 * Interface for downloading a directory as a selection of file objects
 */
public interface ServiceDownloadDirectorySelect {

    ResponseEntity<Resource> download();

    /**
     * Set resource for archiving directory
     * @param map data
     */
    void setMap(HashMap<String, String> map);

}
