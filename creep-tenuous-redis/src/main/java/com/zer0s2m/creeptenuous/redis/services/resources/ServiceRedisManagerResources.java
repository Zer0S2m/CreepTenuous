package com.zer0s2m.creeptenuous.redis.services.resources;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;

import java.util.List;

/**
 * Gathers resources to modify data in Redis.
 * <p>The {@link ServiceManagerRights} cannot live without this implementation</p>
 */
public interface ServiceRedisManagerResources {

    /**
     * Get data about objects to move
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return result
     */
    List<FileRedis> getResourcesFilesForMove(List<String> ids);

    /**
     * Get data about objects to move
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return result
     */
    List<DirectoryRedis> getResourcesDirectoriesForMove(List<String> ids);
}
