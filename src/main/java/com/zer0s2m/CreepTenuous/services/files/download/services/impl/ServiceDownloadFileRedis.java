package com.zer0s2m.CreepTenuous.services.files.download.services.impl;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.FileRedisRepository;
import com.zer0s2m.CreepTenuous.services.core.BaseServiceFileSystemRedis;

import org.springframework.stereotype.Service;

@Service("service-download-file-redis")
public class ServiceDownloadFileRedis extends BaseServiceFileSystemRedis {
    protected ServiceDownloadFileRedis(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider
    ) {
        super(directoryRedisRepository, fileRedisRepository, jwtProvider);
    }
}
