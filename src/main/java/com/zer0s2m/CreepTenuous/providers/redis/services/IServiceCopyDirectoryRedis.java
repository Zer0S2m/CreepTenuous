package com.zer0s2m.CreepTenuous.providers.redis.services;

import com.zer0s2m.CreepTenuous.utils.containers.ContainerInfoFileSystemObject;

import java.util.List;

public interface IServiceCopyDirectoryRedis {
    /**
     * Copy directory in redis
     * @param attached info directory from source path
     */
    void copy(final List<ContainerInfoFileSystemObject> attached);
}
