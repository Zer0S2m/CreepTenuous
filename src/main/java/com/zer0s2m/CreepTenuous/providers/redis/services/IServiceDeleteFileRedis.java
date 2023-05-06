package com.zer0s2m.CreepTenuous.providers.redis.services;

import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.services.base.IBaseServiceRedis;

import java.nio.file.Path;

public interface IServiceDeleteFileRedis extends IBaseServiceRedis<FileRedis> {
    /**
     * Delete file from redis
     * @param systemPath system path
     * @param systemNameFile system name file
     */
    void delete(Path systemPath, String systemNameFile);
}
