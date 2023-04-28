package com.zer0s2m.CreepTenuous.services.files.upload.services.impl;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.jwt.utils.JwtUtils;
import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.FileRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceCreateFileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceUploadFileRedis;
import com.zer0s2m.CreepTenuous.services.files.upload.containers.ContainerDataUploadFile;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("service-upload-file-redis")
public class ServiceUploadFileRedis implements IServiceUploadFileRedis {

    private final FileRedisRepository redisRepository;

    private final JwtProvider jwtProvider;

    private Claims accessClaims;

    @Autowired
    public ServiceUploadFileRedis(FileRedisRepository redisRepository, JwtProvider jwtProvider) {
        this.redisRepository = redisRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void create(ContainerDataUploadFile dataCreatedFile) {
        String loginUser = accessClaims.get("login", String.class);
        String roleUser = accessClaims.get("role", String.class);

        FileRedis objRedis = IServiceCreateFileRedis.getObjRedis(
                loginUser,
                roleUser,
                dataCreatedFile.nameFile(),
                dataCreatedFile.pathFile().toString()
        );

        push(objRedis);
    }

    @Override
    public void push(FileRedis objRedis) {
        redisRepository.save(objRedis);
    }

    public void setAccessToken(String rawAccessToken) {
        setAccessClaims(JwtUtils.getPureAccessToken(rawAccessToken));
    }

    protected void setAccessClaims(String accessToken) {
        this.accessClaims = jwtProvider.getAccessClaims(accessToken);
    }
}
