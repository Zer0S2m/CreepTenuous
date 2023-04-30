package com.zer0s2m.CreepTenuous.providers.redis.services;

import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.services.base.IBaseServiceRedis;

import java.nio.file.Path;
import java.util.List;

public interface IServiceMoveFileRedis extends IBaseServiceRedis<FileRedis> {
    void move(Path systemPath, String systemNameFile);

    void move(Path systemPath, List<String> systemNameFile);
}
