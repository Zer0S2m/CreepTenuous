package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

/**
 * Utilities for getting resources from Redis for downloading directories and file system objects
 */
interface UtilsResourcesDownloadDirectory {

    /**
     * Get info directory for download directory
     * @param systemPathObject system path object ids {@link DirectoryRedis#getRealName()}
     *                         or {@link FileRedis#getRealName()}
     * @return map info directory
     *         <p><b>Key</b> - system name file object</p>
     *         <p><b>Value</b> - real name system file object</p>
     */
    static @NotNull HashMap<String, String> getResource(List<String> systemPathObject,
                                                        @NotNull DirectoryRedisRepository directoryRedisRepository,
                                                        @NotNull FileRedisRepository fileRedisRepository) {
        HashMap<String, String> map = new HashMap<>();

        directoryRedisRepository.findAllById(systemPathObject)
                .forEach(objRedis -> map.put(objRedis.getSystemName(), objRedis.getRealName()));
        fileRedisRepository.findAllById(systemPathObject)
                .forEach(objRedis -> map.put(objRedis.getSystemName(), objRedis.getRealName()));

        return map;
    }

}
