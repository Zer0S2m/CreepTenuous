package com.zer0s2m.CreepTenuous.providers.redis.services;

import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;

import java.util.HashMap;
import java.util.List;

public interface IServiceDownloadDirectoryRedis {
    /**
     * Get info directory for download directory
     * @param systemPathDirectory system path object ids {@link DirectoryRedis#getRealNameDirectory()}
     *                            or {@link FileRedis#getRealNameFile()}
     * @return map info directory
     *         <p><b>Key</b> - system name file object</p>
     *         <p><b>Value</b> - real name system file object</p>
     */
    HashMap<String, String> getResource(List<String> systemPathDirectory);
}
