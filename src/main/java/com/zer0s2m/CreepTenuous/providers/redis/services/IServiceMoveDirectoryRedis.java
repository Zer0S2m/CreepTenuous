package com.zer0s2m.CreepTenuous.providers.redis.services;

import com.zer0s2m.CreepTenuous.services.directory.move.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.CreepTenuous.services.directory.move.containers.ContainerMoveDirectory;

import java.nio.file.Path;
import java.util.List;

public interface IServiceMoveDirectoryRedis {
    /**
     * Move directories in redis
     * @param targetSystemPath target path
     * @param sourceSystemPath source path
     * @param attachedSourceSystem info directory from source path
     * @param systemNameDirectory system name directory
     */
    void move(
            Path targetSystemPath,
            Path sourceSystemPath,
            List<ContainerInfoFileSystemObject> attachedSourceSystem,
            String systemNameDirectory
    );

    /**
     * Move directories in redis
     * @param container container data included data {@link IServiceMoveDirectoryRedis#move(Path, Path, List, String)}
     */
    default void move(ContainerMoveDirectory container) {
        move(container.target(), container.source(), container.attached(), container.systemNameDirectory());
    }
}
