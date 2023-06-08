package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceRedis;

import java.nio.file.Path;

public interface ServiceDeleteFileRedis extends BaseServiceRedis<FileRedis> {
    /**
     * Delete file from redis
     * @param systemPath system path
     * @param systemNameFile system name file
     */
    void delete(Path systemPath, String systemNameFile);
}