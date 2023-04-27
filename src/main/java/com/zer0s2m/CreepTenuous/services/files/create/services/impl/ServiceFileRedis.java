package com.zer0s2m.CreepTenuous.services.files.create.services.impl;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.jwt.utils.JwtUtils;
import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.FileRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceFileRedis;
import com.zer0s2m.CreepTenuous.services.files.create.containers.ContainerDataCreatedFile;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("service-file-redis")
public class ServiceFileRedis implements IServiceFileRedis {
    private final FileRedisRepository redisRepository;

    private final JwtProvider jwtProvider;

    private Claims accessClaims;

    @Autowired
    public ServiceFileRedis(FileRedisRepository redisRepository, JwtProvider jwtProvider) {
        this.redisRepository = redisRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void create(ContainerDataCreatedFile dataCreatedFile) {
        String loginUser = accessClaims.get("login", String.class);
        String roleUser = accessClaims.get("role", String.class);

        FileRedis objRedis = IServiceFileRedis.getObjRedis(
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
