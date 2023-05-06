package com.zer0s2m.CreepTenuous.services.directory.copy.services;

import com.zer0s2m.CreepTenuous.utils.containers.ContainerInfoFileSystemObject;

import java.io.IOException;
import java.util.List;

public interface IServiceCopyDirectory {
    /**
     * Copy directory
     * @param systemParents system path part directories
     * @param systemToParents system path part directories
     * @param systemNameDirectory system name directory
     * @param method method copy {@link com.zer0s2m.CreepTenuous.services.directory.copy.enums.MethodCopyDirectory}
     * @return information about copied file system objects
     * @throws IOException error system
     */
    List<ContainerInfoFileSystemObject> copy(
            List<String> systemParents,
            List<String> systemToParents,
            String systemNameDirectory,
            Integer method
    ) throws IOException;
}
