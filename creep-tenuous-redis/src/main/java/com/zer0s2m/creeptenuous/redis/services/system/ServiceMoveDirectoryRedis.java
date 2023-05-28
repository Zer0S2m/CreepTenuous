package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataMoveDirectory;

import java.util.List;

public interface ServiceMoveDirectoryRedis {
    /**
     * Move directories in redis
     * @param attachedSourceSystem info directory from source path
     */
    void move(List<ContainerInfoFileSystemObject> attachedSourceSystem);

    /**
     * Move directories in redis
     * @param container container data included data {@link ServiceMoveDirectoryRedis#move(List)}
     */
    default void move(ContainerDataMoveDirectory container) {
        move(container.attached());
    }
}
