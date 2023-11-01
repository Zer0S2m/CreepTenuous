package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataDownloadFile;
import com.zer0s2m.creeptenuous.core.balancer.exceptions.FileIsDirectoryException;

import java.io.IOException;
import java.nio.file.Path;
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
    ContainerDataDownloadFile<Path, String> download(
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
    ContainerDataDownloadFile<Path, String> downloadFragment(
            List<String> systemParents, String systemNameFile) throws IOException, FileIsDirectoryException;

}
