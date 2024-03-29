package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;

import java.util.List;

/**
 * Service for deleting file system objects from Redis
 */
public interface ServiceDeleteDirectoryRedis extends BaseServiceFileSystemRedisManagerRightsAccess {

    /**
     * Delete object in redis
     * @param nameFileSystemObject system name directory id {@link DirectoryRedis#getSystemName()}
     */
    default void delete(String nameFileSystemObject) {
        delete(List.of(nameFileSystemObject));
    }

    /**
     * Delete object in redis
     * @param namesFileSystemObject system names directory id {@link DirectoryRedis#getSystemName()}
     */
    void delete(List<String> namesFileSystemObject);

}
