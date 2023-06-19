package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.core.services.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.services.system.CollectZipDirectory;
import com.zer0s2m.creeptenuous.services.system.ServiceDownloadDirectorySelect;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

/**
 * Service for downloading a catalog as a selection of file objects
 */
@ServiceFileSystem("service-download-directory-select")
public class ServiceDownloadDirectorySelectImpl
        implements ServiceDownloadDirectorySelect, CollectZipDirectory, AtomicServiceFileSystem {

    /**
     * info directory
     */
    private HashMap<String, String> map = null;

    @Override
    public ResponseEntity<Resource> download() {

    }

    /**
     * Set resource for archiving directory
     * @param map data
     */
    @Override
    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }

}
