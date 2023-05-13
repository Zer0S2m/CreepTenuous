package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataMoveDirectory;
import com.zer0s2m.creeptenuous.common.enums.MethodMoveDirectory;

import java.io.IOException;
import java.util.List;

public interface ServiceMoveDirectory {
    /**
     * Move directory
     * @param systemParents system path part directories
     * @param systemToParents system path part directories
     * @param systemNameDirectory system name directory
     * @param method method moving {@link MethodMoveDirectory}
     * @return info target and source path
     * @throws IOException error system
     */
    ContainerDataMoveDirectory move(
            List<String> systemParents,
            List<String> systemToParents,
            String systemNameDirectory,
            Integer method
    ) throws IOException;
}
