package com.zer0s2m.CreepTenuous.services.directory.delete.services.impl;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.FileRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceDeleteDirectoryRedis;
import com.zer0s2m.CreepTenuous.services.core.BaseServiceFileSystemRedis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("delete-directory-redis")
public class ServiceDeleteDirectoryRedis extends BaseServiceFileSystemRedis implements IServiceDeleteDirectoryRedis {
    @Autowired
    public ServiceDeleteDirectoryRedis(
            DirectoryRedisRepository redisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider
    ) {
        super(redisRepository, fileRedisRepository, jwtProvider);
    }

    @Override
    public void push(DirectoryRedis objRedis) {
        directoryRedisRepository.delete(objRedis);
    }

    /**
     * Delete object in redis
     * @param systemNameDirectory system name directory id {@link DirectoryRedis#getRealNameDirectory()}
     */
    @Override
    public void delete(String systemNameDirectory) {
        Optional<DirectoryRedis> objRedis = directoryRedisRepository.findById(systemNameDirectory);
        objRedis.ifPresent(this::push);
    }
}
