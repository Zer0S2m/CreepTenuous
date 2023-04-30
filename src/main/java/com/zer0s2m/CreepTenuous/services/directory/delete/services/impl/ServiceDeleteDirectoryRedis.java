package com.zer0s2m.CreepTenuous.services.directory.delete.services.impl;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.FileRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceDeleteDirectoryRedis;
import com.zer0s2m.CreepTenuous.services.core.BaseServiceFileSystemRedis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("delete-directory-redis")
public class ServiceDeleteDirectoryRedis extends BaseServiceFileSystemRedis implements IServiceDeleteDirectoryRedis {
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public ServiceDeleteDirectoryRedis(
            DirectoryRedisRepository redisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider,
            StringRedisTemplate redisTemplate
    ) {
        super(redisRepository, fileRedisRepository, jwtProvider);

        this.redisTemplate = redisTemplate;
    }

    @Override
    public void push(DirectoryRedis objRedis) {
        redisTemplate.delete(objRedis.getSystemNameDirectory());
    }

    @Override
    public void delete(List<String> systemParents, String systemNameDirectory) {
        Optional<DirectoryRedis> objRedis = directoryRedisRepository.findById(systemNameDirectory);
        objRedis.ifPresent(this::push);
    }
}
