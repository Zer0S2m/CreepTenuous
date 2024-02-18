package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataDownloadFile;
import com.zer0s2m.creeptenuous.core.balancer.FileBalancer;
import com.zer0s2m.creeptenuous.core.balancer.FileIsDirectoryException;
import com.zer0s2m.creeptenuous.core.balancer.FileSplit;
import com.zer0s2m.creeptenuous.services.system.ServiceDownloadFile;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

/**
 * A service for downloading regular files and fragmented files.
 */
@ServiceFileSystem("service-download-file")
public class ServiceDownloadFileImpl implements ServiceDownloadFile {

    private final ServiceBuildDirectoryPath buildDirectoryPath = new ServiceBuildDirectoryPath();

    /**
     * Get resource for download file.
     *
     * @param systemParents  System names of directories from which the directory path will be
     *                       collected where the file.
     * @param systemNameFile System name file.
     * @return Container data download file.
     * @throws IOException signals that an I/O exception to some sort has occurred.
     */
    @Override
    public ContainerDataDownloadFile<Path, String> download(
            List<String> systemParents,
            String systemNameFile
    ) throws IOException {
        Path pathFile = Paths.get(buildDirectoryPath.build(systemParents), systemNameFile);
        if (!Files.exists(pathFile)) {
            throw new NoSuchFileException(pathFile.getFileName().toString());
        }

        return new ContainerDataDownloadFile<>(
                pathFile,
                null,
                systemNameFile
        );
    }

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
    @Override
    public ContainerDataDownloadFile<Path, String> downloadFragment(
            List<String> systemParents, String systemNameFile) throws IOException, FileIsDirectoryException {
        Path pathFile = Paths.get(buildDirectoryPath.build(systemParents), systemNameFile);
        Set<Path> partsFragments = FileBalancer.getAllParts(pathFile);
        Path outputFile = FileBalancer.merge(new FileSplit(partsFragments), pathFile);

        return new ContainerDataDownloadFile<>(
                outputFile,
                null,
                systemNameFile
        );
    }

}
