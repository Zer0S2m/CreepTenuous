package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.security.jwt.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisManagerRightsAccessImpl;
import org.springframework.stereotype.Service;

/**
 * Service for downloading file system objects from Redis.
 */
@Service("service-download-file-redis")
public class ServiceDownloadFileRedisImpl extends BaseServiceFileSystemRedisManagerRightsAccessImpl {

    protected ServiceDownloadFileRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository,
            JwtProvider jwtProvider) {
        super(directoryRedisRepository, fileRedisRepository, frozenFileSystemObjectRedisRepository,
                jwtProvider);
    }

}
