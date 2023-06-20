package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectorySelectPartApi;
import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.handlers.impl.ServiceFileSystemExceptionHandlerOperationDownload;
import com.zer0s2m.creeptenuous.core.services.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.core.services.Distribution;
import com.zer0s2m.creeptenuous.services.system.CollectZipDirectory;
import com.zer0s2m.creeptenuous.services.system.ServiceDownloadDirectorySelect;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Service for downloading a catalog as a selection of file objects
 */
@ServiceFileSystem("service-download-directory-select")
@CoreServiceFileSystem(method = "download")
public class ServiceDownloadDirectorySelectImpl extends ServiceDownloadDirectorySetHeadersImpl
        implements ServiceDownloadDirectorySelect, CollectZipDirectory, AtomicServiceFileSystem {

    private final Logger logger = LogManager.getLogger(ServiceDownloadDirectorySelectImpl.class);

    private final ServiceBuildDirectoryPath buildDirectoryPath;

    /**
     * info directory
     */
    private HashMap<String, String> map = null;

    @Autowired
    public ServiceDownloadDirectorySelectImpl(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    /**
     * Download selectively file objects
     * @param data object file system information
     * @return archive zip
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     */
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
    public ResponseEntity<Resource> download(@NotNull List<DataDownloadDirectorySelectPartApi> data) throws IOException {
        List<Path> paths = new ArrayList<>();
        data.forEach(obj -> {
            try {
                Path source = Paths.get(buildDirectoryPath.build(obj.systemParents()), obj.systemName());
                Path pathToZip = collectZip(source, this.map, this.getClass().getCanonicalName());
                paths.add(pathToZip);
            } catch (NoSuchFileException e) {
                throw new RuntimeException(e);
            }
        });

        Path collectZip = mergeZipArchives(paths);
        ByteArrayResource contentBytes = new ByteArrayResource(Files.readAllBytes(collectZip));

        paths.forEach(this::deleteFileZip);
        deleteFileZip(collectZip);

        return ResponseEntity.ok()
                .headers(collectHeaders(collectZip, contentBytes))
                .body(contentBytes);
    }

    /**
     * Combine zip archives from their paths
     * @param paths zip archive paths
     * @return merged archive path
     * @throws NoSuchFileException file that does not exist.
     */
    private @NotNull Path mergeZipArchives(@NotNull List<Path> paths) throws NoSuchFileException {
        final ZipFile zipFile = new ZipFile(
                Path.of(buildDirectoryPath.build(), "data-" + Distribution.getUUID() + ".zip").toFile());

        paths.forEach(path -> {
            ZipFile zipFileToMerge = new ZipFile(new File(path.toString()));
            try {
                for (FileHeader fileHeader : zipFileToMerge.getFileHeaders()) {
                    try (InputStream inputStream = zipFileToMerge.getInputStream(fileHeader)) {
                        ZipParameters zipParameters = getZipParametersFromFileHeader(fileHeader);
                        zipFile.addStream(inputStream, zipParameters);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return zipFile.getFile().toPath();
    }

    private @NotNull ZipParameters getZipParametersFromFileHeader(@NotNull FileHeader fileHeader) {
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionMethod(fileHeader.getCompressionMethod());
        zipParameters.setFileNameInZip(fileHeader.getFileName());
        return zipParameters;
    }

    /**
     * Delete zip archive
     * @param source source zip archive
     */
    private void deleteFileZip(@NotNull Path source) {
        boolean isDeleted = source.toFile().delete();
        logger.info("Is deleted zip file: " + isDeleted);
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
