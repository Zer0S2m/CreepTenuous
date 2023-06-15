package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;

import java.util.List;

/**
 * Service for viewing file system objects in Redis
 */
public interface ServiceManagerDirectoryRedis extends BaseServiceFileSystemRedisManagerRightsAccess {

    /**
     * Get data file system object
     * @param systemNamesFileSystemObject system path object ids {@link DirectoryRedis#getRealNameDirectory()}
     *                                    or {@link FileRedis#getRealNameFile()}
     * @return json array
     */
    List<Object> build(List<String> systemNamesFileSystemObject);

}
