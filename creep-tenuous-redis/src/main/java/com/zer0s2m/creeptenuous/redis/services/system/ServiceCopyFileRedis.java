package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceRedis;

import java.nio.file.Path;
import java.util.List;

public interface ServiceCopyFileRedis extends BaseServiceRedis<FileRedis> {
    /**
     * Copy files in redis
     * @param target new system directory path
     * @param existingSystemNameFile existing system file names
     * @param newSystemFileName new system file names
     */
    void copy(List<Path> target, List<String> existingSystemNameFile, List<String> newSystemFileName);

    /**
     * Copy file in redis
     * @param target new system directory path
     * @param existingSystemNameFile existing system file names
     * @param newSystemFileName new system file names
     */
    default void copy(Path target, String existingSystemNameFile, String newSystemFileName) {
        copy(List.of(target), List.of(existingSystemNameFile), List.of(newSystemFileName));
    }
}
