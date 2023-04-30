package com.zer0s2m.CreepTenuous.services.files.create.services.impl;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.FileRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceCreateFileRedis;
import com.zer0s2m.CreepTenuous.services.core.BaseServiceFileSystemRedis;
import com.zer0s2m.CreepTenuous.services.files.create.containers.ContainerDataCreatedFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("service-file-redis")
public class ServiceCreateFileRedis extends BaseServiceFileSystemRedis implements IServiceCreateFileRedis {
    private final FileRedisRepository fileRedisRepository;

    @Autowired
    public ServiceCreateFileRedis(
            FileRedisRepository fileRedisRepository,
            DirectoryRedisRepository directoryRedisRepository,
            JwtProvider jwtProvider
    ) {
        super(directoryRedisRepository, jwtProvider);

        this.fileRedisRepository = fileRedisRepository;
    }

    @Override
    public void create(ContainerDataCreatedFile dataCreatedFile) {
        String loginUser = accessClaims.get("login", String.class);
        String roleUser = accessClaims.get("role", String.class);

        FileRedis objRedis = IServiceCreateFileRedis.getObjRedis(
                loginUser,
                roleUser,
                dataCreatedFile.realNameFile(),
                dataCreatedFile.systemNameFile(),
                dataCreatedFile.systemPathFile().toString()
        );

        push(objRedis);
    }

    @Override
    public void push(FileRedis objRedis) {
        fileRedisRepository.save(objRedis);
    }
}
