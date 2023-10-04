package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.handlers.impl.ServiceFileSystemExceptionHandlerOperationDownload;
import com.zer0s2m.creeptenuous.core.atomic.services.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.services.system.CollectZipDirectory;
import com.zer0s2m.creeptenuous.services.system.ServiceDownloadDirectory;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
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

    private final ServiceBuildDirectoryPath buildDirectoryPath;

    /**
     * info directory
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
            name = "download-directory",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            exception = IOException.class,
                            handler = ServiceFileSystemExceptionHandlerOperationDownload.class,
                            operation = ContextAtomicFileSystem.Operations.DOWNLOAD
                    )
            }
    )
    public Path download(List<String> systemParents, String systemNameDirectory) throws IOException {
        Path source = Paths.get(buildDirectoryPath.build(systemParents), systemNameDirectory);
        return collectZip(source, this.map, this.getClass().getCanonicalName());
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
