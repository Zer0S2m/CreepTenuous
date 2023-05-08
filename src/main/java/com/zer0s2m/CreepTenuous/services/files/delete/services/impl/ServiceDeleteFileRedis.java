package com.zer0s2m.CreepTenuous.services.files.delete.services.impl;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.FileRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceDeleteFileRedis;
import com.zer0s2m.CreepTenuous.services.core.BaseServiceFileSystemRedis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Optional;

@Service("service-delete-file-redis")
public class ServiceDeleteFileRedis extends BaseServiceFileSystemRedis implements IServiceDeleteFileRedis {
    @Autowired
    public ServiceDeleteFileRedis(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider
    ) {
        super(directoryRedisRepository, fileRedisRepository, jwtProvider);
    }

    /**
     * Delete file from redis
     * @param systemPath system path
     * @param systemNameFile system name file
     */
    @Override
    public void delete(Path systemPath, String systemNameFile) {
        Optional<FileRedis> objRedis = fileRedisRepository.findById(systemNameFile);
        objRedis.ifPresent(this::push);
    }

    @Override
    public void push(FileRedis objRedis) {
        fileRedisRepository.delete(objRedis);
    }
}
