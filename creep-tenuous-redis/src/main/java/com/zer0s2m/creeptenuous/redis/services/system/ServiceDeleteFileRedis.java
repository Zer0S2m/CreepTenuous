package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceRedis;

import java.nio.file.Path;

/**
 * Service for deleting file system objects from Redis
 */
public interface ServiceDeleteFileRedis extends BaseServiceRedis<FileRedis>, BaseServiceFileSystemRedis {

    /**
     * Delete file from redis
     * @param systemPath system path
     * @param systemNameFile system name file
     */
    void delete(Path systemPath, String systemNameFile);

}
