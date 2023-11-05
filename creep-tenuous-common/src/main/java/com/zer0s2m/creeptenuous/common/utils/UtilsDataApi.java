package com.zer0s2m.creeptenuous.common.utils;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataDownloadFile;
import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectorySelectPartApi;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utilities for working with incoming data in the API
 */
public interface UtilsDataApi {

    ConfigurableMimeFileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();

    /**
     * Collect unique parts of the system path from information about file system objects
     * @param attached file system object data
     * @return unique parts of the system path
     */
    @Contract(pure = true)
    static @NotNull List<String> collectUniqueSystemParentsForDownloadDirectorySelect(
            @NotNull List<DataDownloadDirectorySelectPartApi> attached) {
        Set<String> uniqueSystemParents = new HashSet<>();
        attached.forEach(attach -> uniqueSystemParents.addAll(attach.systemParents()));
        return uniqueSystemParents
                .stream()
                .toList();
    }

    /**
     * Collect unique system names from information about file system objects
     * @param attached file system object data
     * @return unique system names
     */
    static List<String> collectUniqueSystemNameForDownloadDirectorySelect(
            @NotNull List<DataDownloadDirectorySelectPartApi> attached) {
        Set<String> uniqueSystemName = new HashSet<>();
        attached.forEach(attach -> uniqueSystemName.add(attach.systemName()));
        return uniqueSystemName
                .stream()
                .toList();
    }

    /**
     * Clear file extensions.
     * @param fileSystemObjects File system objects.
     * @return Stripped file object names from extension.
     */
    static List<String> clearFileExtensions(@NotNull List<String> fileSystemObjects) {
        return fileSystemObjects
                .stream()
                .map(systemName -> {
                    if (systemName.contains(".")) {
                        String[] splitSystemName = systemName.split("\\.");
                        return splitSystemName[0];
                    }
                    return systemName;
                })
                .toList();
    }

    /**
     * Clear file extensions.
     * @param fileSystemObjects File system object.
     * @return Stripped file object names from extension.
     */
    static String clearFileExtensions(String fileSystemObjects) {
        return clearFileExtensions(List.of(fileSystemObjects)).get(0);
    }

    /**
     * Collect headers for download directory.
     * @param path Source archive zip.
     * @return Headers.
     */
    static @NotNull HttpHeaders collectHeadersForZip(@NotNull Path path) {
        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "1");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition
                .attachment()
                .filename(String.valueOf(path.getFileName()))
                .build()
                .toString()
        );

        headers.add(HttpHeaders.CONTENT_TYPE, Directory.TYPE_APPLICATION_ZIP.get());

        return headers;
    }

    /**
     * Collect headers for request.
     *
     * @param data Download file information.
     * @return Ready-made headers for the request.
     */
    static @NotNull HttpHeaders collectHeadersForFile(
            @NotNull ContainerDataDownloadFile<Path, String> data) {
        final HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition
                .attachment()
                .filename(data.filename())
                .build()
                .toString()
        );
        headers.add(
                HttpHeaders.CONTENT_TYPE,
                data.mimeType() == null ? getMimeTypePathBased(data.byteContent()) : data.mimeType());

        return headers;
    }

    @Contract(pure = true)
    static @NotNull StreamingResponseBody getStreamingResponseBodyFromPath(Path source) {
        return outputStream -> Files.copy(source, outputStream);
    }

    static @NotNull String getMimeTypePathBased(@NotNull Path source) {
        return fileTypeMap.getContentType(source.toString());
    }

}
