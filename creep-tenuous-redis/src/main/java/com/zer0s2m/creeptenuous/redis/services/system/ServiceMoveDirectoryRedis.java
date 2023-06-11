package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataMoveDirectory;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedis;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Service for servicing the movement of file system objects in Redis
 */
public interface ServiceMoveDirectoryRedis extends BaseServiceFileSystemRedis {

    /**
     * Move directories in redis
     * @param attachedSourceSystem info directory from source path
     */
    void move(List<ContainerInfoFileSystemObject> attachedSourceSystem);

    /**
     * Move directories in redis
     * @param container container data included data {@link ServiceMoveDirectoryRedis#move(List)}
     */
    default void move(@NotNull ContainerDataMoveDirectory container) {
        move(container.attached());
    }

}
