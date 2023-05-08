package com.zer0s2m.CreepTenuous.services.directory.move.services;

import com.zer0s2m.CreepTenuous.services.directory.move.containers.ContainerMoveDirectory;
import com.zer0s2m.CreepTenuous.services.directory.move.enums.MethodMoveDirectory;

import java.io.IOException;
import java.util.List;

public interface IServiceMoveDirectory {
    /**
     * Move directory
     * @param systemParents system path part directories
     * @param systemToParents system path part directories
     * @param systemNameDirectory system name directory
     * @param method method moving {@link MethodMoveDirectory}
     * @return info target and source path
     * @throws IOException error system
     */
    ContainerMoveDirectory move(
            List<String> systemParents,
            List<String> systemToParents,
            String systemNameDirectory,
            Integer method
    ) throws IOException;
}
