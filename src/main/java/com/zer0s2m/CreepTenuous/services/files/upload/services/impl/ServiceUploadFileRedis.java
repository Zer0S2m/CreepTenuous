package com.zer0s2m.CreepTenuous.services.files.upload.services.impl;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.FileRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceCreateFileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceUploadFileRedis;
import com.zer0s2m.CreepTenuous.services.core.BaseServiceFileSystemRedis;
import com.zer0s2m.CreepTenuous.services.files.upload.containers.ContainerDataUploadFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("service-upload-file-redis")
public class ServiceUploadFileRedis extends BaseServiceFileSystemRedis implements IServiceUploadFileRedis {
    @Autowired
    public ServiceUploadFileRedis(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider
    ) {
        super(directoryRedisRepository, fileRedisRepository, jwtProvider);
    }

    @Override
    public void create(ContainerDataUploadFile dataCreatedFile) {
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
