package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;

import java.util.List;

public interface ServiceManagerDirectoryRedis {
    /**
     * Get data file system object
     * @param systemNamesFileSystemObject system path object ids {@link DirectoryRedis#getRealNameDirectory()}
     *                                    or {@link FileRedis#getRealNameFile()}
     * @return json array
     */
    List<Object> build(List<String> systemNamesFileSystemObject);
}
