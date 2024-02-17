package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectorySelectPartApi;
import com.zer0s2m.creeptenuous.core.atomic.AtomicServiceFileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

/**
 * Interface for downloading a directory as a selection of file objects
 */
public interface ServiceDownloadDirectorySelect extends CollectZipDirectory, AtomicServiceFileSystem {

    /**
     * Download selectively file objects
     * @param data object file system information
     * @return archive zip (source)
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     */
    Path download(List<DataDownloadDirectorySelectPartApi> data) throws IOException;

    /**
     * Download selectively file objects.
     *
     * @param data Object file system information.
     * @return archive zip (source).
     * @throws IOException Signals that an I/O exception to some sort has occurred.
     */
    Path downloadFromContainers(Iterable<ContainerInfoFileSystemObject> data) throws IOException;

    /**
     * Set resource for archiving directory
     * @param map data
     */
    void setMap(HashMap<String, String> map);

}
