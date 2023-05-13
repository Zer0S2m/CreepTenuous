package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataMoveDirectory;

import java.nio.file.Path;
import java.util.List;

public interface ServiceMoveDirectoryRedis {
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
     * @param container container data included data {@link ServiceMoveDirectoryRedis#move(Path, Path, List, String)}
     */
    default void move(ContainerDataMoveDirectory container) {
        move(container.target(), container.source(), container.attached(), container.systemNameDirectory());
    }
}
