package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.handlers.impl.ServiceFileSystemExceptionHandlerOperationDownload;
import com.zer0s2m.creeptenuous.core.services.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.services.system.CollectZipDirectory;
import com.zer0s2m.creeptenuous.services.system.ServiceDownloadDirectorySelect;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;

/**
 * Service for downloading a catalog as a selection of file objects
 */
@ServiceFileSystem("service-download-directory-select")
@CoreServiceFileSystem(method = "download")
public class ServiceDownloadDirectorySelectImpl
        implements ServiceDownloadDirectorySelect, CollectZipDirectory, AtomicServiceFileSystem {

    /**
     * info directory
     */
    private HashMap<String, String> map = null;

    @Override
    @AtomicFileSystem(
            name = "download-directory-select",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            exception = IOException.class,
                            handler = ServiceFileSystemExceptionHandlerOperationDownload.class,
                            operation = ContextAtomicFileSystem.Operations.DOWNLOAD
                    )
            }
    )
    public ResponseEntity<Resource> download() {
        return ResponseEntity.ok()
                .body(null);
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
