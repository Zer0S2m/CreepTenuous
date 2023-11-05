package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.exceptions.NoSuchFileExistsException;
import com.zer0s2m.creeptenuous.core.atomic.services.AtomicServiceFileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Maintenance service file deletion
 */
public interface ServiceDeleteFile extends AtomicServiceFileSystem {

    /**
     * Delete file from file system
     * @param systemNameFile system name file
     * @param systemParents system path part directories
     * @return source path system
     * @throws IOException error system
     * @throws NoSuchFileExistsException when no file in file system
     */
    Path delete(String systemNameFile, List<String> systemParents) throws IOException, NoSuchFileExistsException;

    /**
     * Delete file from file system
     * @param source source path system
     * @return source path system
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     */
    Path delete(Path source) throws IOException;

}
