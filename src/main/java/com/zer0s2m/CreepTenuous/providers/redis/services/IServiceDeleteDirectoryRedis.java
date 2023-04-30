package com.zer0s2m.CreepTenuous.providers.redis.services;

import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.services.base.IBaseServiceRedis;

import java.util.List;

public interface IServiceDeleteDirectoryRedis extends IBaseServiceRedis<DirectoryRedis> {
    void delete(List<String> systemParents, String systemNameDirectory);
}
