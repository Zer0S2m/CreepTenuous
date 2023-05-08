package com.zer0s2m.CreepTenuous.services.directory.delete.services;

import java.nio.file.NoSuchFileException;
import java.util.List;

public interface IServiceDeleteDirectory {
    /**
     * Delete directory from system
     * @param systemParents parts of the system path - source
     * @param systemName system name directory
     * @throws NoSuchFileException system error
     */
    void delete(List<String> systemParents, String systemName) throws NoSuchFileException;
}
