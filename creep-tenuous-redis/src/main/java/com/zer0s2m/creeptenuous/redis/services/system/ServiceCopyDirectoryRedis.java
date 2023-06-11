package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedis;

import java.util.List;

/**
 * Service for copying file system objects by writing to Redis
 */
public interface ServiceCopyDirectoryRedis extends BaseServiceFileSystemRedis {

    /**
     * Copy directory in redis
     * @param attached info directory from source path
     */
    void copy(final List<ContainerInfoFileSystemObject> attached);

}
