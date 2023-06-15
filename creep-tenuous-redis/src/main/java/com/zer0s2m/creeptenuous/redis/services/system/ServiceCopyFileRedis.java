package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceRedis;

import java.nio.file.Path;
import java.util.List;

/**
 * Service for copying file system objects by writing to Redis
 */
public interface ServiceCopyFileRedis extends BaseServiceRedis<FileRedis>, BaseServiceFileSystemRedisManagerRightsAccess {

    /**
     * Copy files in redis
     * @param target new system directory path
     * @param existingSystemNameFile existing system file names
     * @param newSystemFileName new system file names
     * @return result copy file(s)
     */
    List<FileRedis> copy(List<Path> target, List<String> existingSystemNameFile, List<String> newSystemFileName);

    /**
     * Copy file in redis
     * @param target new system directory path
     * @param existingSystemNameFile existing system file names
     * @param newSystemFileName new system file names
     * @return result copy file(s)
     */
    default List<FileRedis> copy(Path target, String existingSystemNameFile, String newSystemFileName) {
        return copy(List.of(target), List.of(existingSystemNameFile), List.of(newSystemFileName));
    }

}
