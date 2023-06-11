package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceRedis;

/**
 * Service for deleting file system objects from Redis
 */
public interface ServiceDeleteDirectoryRedis extends BaseServiceRedis<DirectoryRedis> {

    /**
     * Delete object in redis
     * @param systemNameDirectory system name directory id {@link DirectoryRedis#getRealNameDirectory()}
     */
    void delete(String systemNameDirectory);

}
