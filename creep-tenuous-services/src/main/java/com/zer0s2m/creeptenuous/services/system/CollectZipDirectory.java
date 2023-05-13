package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDownloadDirectoryRedis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public interface CollectZipDirectory {
    /**
     * Collect archive zip
     * @param source source system path directory
     * @param map info directory (get data - {@link ServiceDownloadDirectoryRedis#getResource(List)})
     *            <p><b>Key</b> - system name file object</p>
     *            <p><b>Value</b> - real name system file object</p>
     * @return path ready archive
     * @throws RuntimeException system error
     */
    default Path collectZip(Path source, HashMap<String, String> map) throws RuntimeException {
        String pathToZip = source.getParent().toString();
        String realPathToZip;

        if (map != null && map.size() > 0) {
            realPathToZip = Paths.get(
                    pathToZip, map.get(source.getFileName().toString()) + Directory.EXTENSION_FILE_ZIP.get()
            ).toString();
        } else {
            realPathToZip = Paths.get(
                    pathToZip, source.getFileName().toString() + Directory.EXTENSION_FILE_ZIP.get()
            ).toString();
        }

        try (
                final FileOutputStream fosFile = new FileOutputStream(realPathToZip);
                final ZipOutputStream fosZip = new ZipOutputStream(fosFile)
        ) {
            File newFileZip = new File(source.toString());
            collectZipFile(newFileZip, newFileZip.getName(), fosZip, map);
            return Paths.get(realPathToZip);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Collect zip archive
     * @param newFileZip file zip archive
     * @param filenameZip filename zip archive
     * @param fosZip output stream
     * @param map info directory (get data - {@link ServiceDownloadDirectoryRedis#getResource(List)})
     *            <p><b>Key</b> - system name file object</p>
     *            <p><b>Value</b> - real name system file object</p>
     * @throws IOException system error
     */
    private void collectZipFile(
            File newFileZip,
            String filenameZip,
            ZipOutputStream fosZip,
            HashMap<String, String> map
    ) throws IOException {
        if (newFileZip.isHidden()) {
            return;
        }

        String readyFilenameZip = buildRealNameSystemObject(filenameZip, map);

        if (newFileZip.isDirectory()) {
            if (filenameZip.endsWith(Directory.SEPARATOR.get())) {
                fosZip.putNextEntry(new ZipEntry(readyFilenameZip));
                fosZip.closeEntry();
            } else {
                fosZip.putNextEntry(new ZipEntry(readyFilenameZip + Directory.SEPARATOR.get()));
                fosZip.closeEntry();
            }

            File[] children = newFileZip.listFiles();

            if (children != null) {
                for (File childFile : children) {
                    collectZipFile(
                            childFile,
                            filenameZip + Directory.SEPARATOR.get() + childFile.getName(),
                            fosZip,
                            map
                    );
                }
            }
            return;
        }

        try (FileInputStream fisZip = new FileInputStream(newFileZip)) {
            ZipEntry zipEntry = new ZipEntry(readyFilenameZip);
            fosZip.putNextEntry(zipEntry);

            final byte[] bytes = new byte[(int) Directory.BYTES_COLLECT_ZIP.getInt()];
            int length;
            while ((length = fisZip.read(bytes)) >= 0) {
                fosZip.write(bytes, 0, length);
            }
        }
    }

    /**
     * Get real system path by map
     * @param rawPath system name path
     * @param map info directory (get data - {@link ServiceDownloadDirectoryRedis#getResource(List)})
     *            <p><b>Key</b> - system name file object</p>
     *            <p><b>Value</b> - real name system file object</p>
     * @return real system path
     */
    private String buildRealNameSystemObject(String rawPath, HashMap<String, String> map) {
        if (map == null) {
            return rawPath;
        } else if (map.size() == 0) {
            return rawPath;
        }

        StringBuilder readyPathStrContainer = new StringBuilder();
        List<String> rawPathPart = Arrays.asList(rawPath.split(Directory.SEPARATOR.get()));

        rawPathPart.forEach((part) -> {
            String realNameFileSystemObject = map.get(part);
            if (realNameFileSystemObject == null) {
                readyPathStrContainer
                        .append(part).append(Directory.SEPARATOR.get());
            } else {
                readyPathStrContainer
                        .append(realNameFileSystemObject).append(Directory.SEPARATOR.get());
            }
        });

        return readyPathStrContainer.substring(0, readyPathStrContainer.length() - 1);
    }
}
