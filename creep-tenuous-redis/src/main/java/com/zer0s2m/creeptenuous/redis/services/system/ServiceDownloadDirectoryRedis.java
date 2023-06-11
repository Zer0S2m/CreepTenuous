package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedis;

import java.util.HashMap;
import java.util.List;

/**
 * Service for downloading file system objects from Redis.
 * <p>Getting resources</p>
 */
public interface ServiceDownloadDirectoryRedis extends BaseServiceFileSystemRedis {

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
