package com.zer0s2m.CreepTenuous.providers.redis.services;

import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;

import java.util.List;

public interface IServiceManagerDirectoryRedis {
    /**
     * Get data file system object
     * @param systemNamesFileSystemObject system path object ids {@link DirectoryRedis#getRealNameDirectory()}
     *                                    or {@link FileRedis#getRealNameFile()}
     * @return json array
     */
    List<Object> build(List<String> systemNamesFileSystemObject);
}
