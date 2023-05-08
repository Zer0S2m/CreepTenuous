package com.zer0s2m.CreepTenuous.providers.redis.services;

import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.services.base.IBaseServiceRedis;

import java.nio.file.Path;
import java.util.List;

public interface IServiceMoveFileRedis extends IBaseServiceRedis<FileRedis> {
    /**
     * Move file in redis
     * @param systemPath system path file
     * @param systemNameFile system name file
     */
    void move(Path systemPath, String systemNameFile);

    /**
     * Move files in redis
     * @param systemPath system path file
     * @param systemNameFile system names files
     */
    void move(Path systemPath, List<String> systemNameFile);
}
