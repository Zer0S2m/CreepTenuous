package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceRedis;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface ServiceMoveFileRedis extends BaseServiceRedis<FileRedis> {
    /**
     * Move file in redis
     * @param systemPath system path file
     * @param systemNameFile system name file
     * @return result move file(s)
     */
    Optional<FileRedis> move(Path systemPath, String systemNameFile);

    /**
     * Move files in redis
     * @param systemPath system path file
     * @param systemNameFile system names files
     * @return result move file(s)
     */
    Iterable<FileRedis> move(Path systemPath, List<String> systemNameFile);
}
