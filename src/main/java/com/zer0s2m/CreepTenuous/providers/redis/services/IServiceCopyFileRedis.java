package com.zer0s2m.CreepTenuous.providers.redis.services;

import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.services.base.IBaseServiceRedis;

import java.nio.file.Path;
import java.util.List;

public interface IServiceCopyFileRedis extends IBaseServiceRedis<FileRedis> {
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
