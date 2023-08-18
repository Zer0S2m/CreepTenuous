package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDownloadDirectoryRedis;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisManagerRightsAccessImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * Service for downloading file system objects from Redis.
 * <p>Getting resources</p>
 */
@Service("service-download-directory-redis")
public class ServiceDownloadDirectoryRedisImpl extends BaseServiceFileSystemRedisManagerRightsAccessImpl
        implements ServiceDownloadDirectoryRedis {

    @Autowired
    public ServiceDownloadDirectoryRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository,
            JwtProvider jwtProvider) {
        super(directoryRedisRepository, fileRedisRepository, frozenFileSystemObjectRedisRepository,
                jwtProvider);
    }

    /**
     * Get info directory for download directory
     * @param systemPathObject system path object ids {@link DirectoryRedis#getRealName()}
     *                         or {@link FileRedis#getRealName()}
     * @return map info directory
     *         <p><b>Key</b> - system name file object</p>
     *         <p><b>Value</b> - real name system file object</p>
     */
    @Override
    public HashMap<String, String> getResource(List<String> systemPathObject) {
        return UtilsResourcesDownloadDirectory.getResource(systemPathObject, directoryRedisRepository,
                fileRedisRepository);
    }

}
