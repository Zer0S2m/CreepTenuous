package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;

import java.util.List;

public interface ServiceCopyDirectoryRedis {
    /**
     * Copy directory in redis
     * @param attached info directory from source path
     */
    void copy(final List<ContainerInfoFileSystemObject> attached);
}
