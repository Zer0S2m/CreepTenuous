package com.zer0s2m.CreepTenuous.services.directory.manager.services;

import com.zer0s2m.CreepTenuous.services.directory.manager.containers.ContainerDataBuilder;
import com.zer0s2m.CreepTenuous.services.directory.manager.exceptions.NotValidLevelDirectoryException;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

public interface IServiceManagerDirectory {
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
     * @throws NoSuchFieldException not exists directory
     */
    ContainerDataBuilder build(List<String> systemParents, Integer level)
            throws IOException, NotValidLevelDirectoryException, NoSuchFieldException;
}
