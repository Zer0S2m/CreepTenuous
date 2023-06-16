package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataBuilderDirectory;
import com.zer0s2m.creeptenuous.common.exceptions.NotValidLevelDirectoryException;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

/**
 * A service for servicing all information and operations on file system objects. Use in end endpoints
 */
public interface ServiceManagerDirectory {

    /**
     * Get directory
     * @return source path (string)
     * @throws NoSuchFileException not exists directory
     */
    String getDirectory() throws NoSuchFileException;

    /**
     * Get source path directory
     * @return parts of the system path - source
     */
    List<String> getSystemParents();

    /**
     * Set source path directory
     * @param systemParents parts of the system path - source
     */
    void setSystemParents(List<String> systemParents);

    /**
     * Build data in directory
     * @param systemParents parts of the system path - source
     * @param level level directory
     * @return data in directory
     * @throws IOException system error
     * @throws NotValidLevelDirectoryException invalid param directory level
     */
    ContainerDataBuilderDirectory build(List<String> systemParents, Integer level)
            throws IOException, NotValidLevelDirectoryException;

}
