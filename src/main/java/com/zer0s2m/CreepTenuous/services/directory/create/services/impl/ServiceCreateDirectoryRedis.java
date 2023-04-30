package com.zer0s2m.CreepTenuous.services.directory.create.services.impl;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.FileRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceCreateDirectoryRedis;
import com.zer0s2m.CreepTenuous.services.core.BaseServiceFileSystemRedis;
import com.zer0s2m.CreepTenuous.services.directory.create.containers.ContainerDataCreatedDirectory;
import com.zer0s2m.CreepTenuous.services.directory.create.exceptions.FileAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Objects;

@Service("service-directory-redis")
public class ServiceCreateDirectoryRedis extends BaseServiceFileSystemRedis implements IServiceCreateDirectoryRedis {
    @Autowired
    public ServiceCreateDirectoryRedis(
            DirectoryRedisRepository redisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider) {
        super(redisRepository, fileRedisRepository, jwtProvider);
    }

    public void create(ContainerDataCreatedDirectory dataCreatedDirectory) {
        String loginUser = accessClaims.get("login", String.class);
        String roleUser = accessClaims.get("role", String.class);

        DirectoryRedis objRedis = IServiceCreateDirectoryRedis.getObjRedis(
                loginUser,
                roleUser,
                dataCreatedDirectory.realNameDirectory(),
                dataCreatedDirectory.systemNameDirectory(),
                dataCreatedDirectory.pathDirectory().toString()
        );
        push(objRedis);
    }

    @Override
    public void push(DirectoryRedis objRedis) {
        directoryRedisRepository.save(objRedis);
    }

    @Override
    public void checkIsExistsDirectory(Path systemSource, String nameDirectory) {
        // TODO: finalize
        directoryRedisRepository.findAllByRealNameDirectory(nameDirectory).forEach((fileRedis) -> {
            if ((Objects.equals(fileRedis.getRealNameDirectory(), nameDirectory) &&
                    Objects.equals(fileRedis.getPathDirectory(), systemSource.toString()))) {
                throw new FileAlreadyExistsException();
            }
        });
    }
}
