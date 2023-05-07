package com.zer0s2m.CreepTenuous.providers.redis.services;

import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.services.base.IBaseServiceRedis;

public interface IServiceDeleteDirectoryRedis extends IBaseServiceRedis<DirectoryRedis> {
    /**
     * Delete object in redis
     * @param systemNameDirectory system name directory id {@link DirectoryRedis#getRealNameDirectory()}
     */
    void delete(String systemNameDirectory);
}
