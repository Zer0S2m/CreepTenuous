package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.enums.MethodCopyDirectory;

import java.io.IOException;
import java.util.List;

/**
 * Directory copy service
 */
public interface ServiceCopyDirectory {

    /**
     * Copy directory
     * @param systemParents system path part directories
     * @param systemToParents system path part directories
     * @param systemNameDirectory system name directory
     * @param method method copy {@link MethodCopyDirectory}
     * @return information about copied file system objects
     * @throws IOException error system
     */
    List<ContainerInfoFileSystemObject> copy(List<String> systemParents, List<String> systemToParents,
                                             String systemNameDirectory, Integer method) throws IOException;

}
