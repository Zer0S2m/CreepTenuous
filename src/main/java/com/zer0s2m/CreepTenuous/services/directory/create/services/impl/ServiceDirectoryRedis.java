package com.zer0s2m.CreepTenuous.services.directory.create.services.impl;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.jwt.utils.JwtUtils;
import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceDirectoryRedis;
import com.zer0s2m.CreepTenuous.services.directory.create.containers.ContainerDataCreatedDirectory;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("service-directory-redis")
public class ServiceDirectoryRedis implements IServiceDirectoryRedis {
    private final JwtProvider jwtProvider;

    private final DirectoryRedisRepository redisRepository;

    private Claims accessClaims;

    @Autowired
    public ServiceDirectoryRedis(JwtProvider jwtProvider, DirectoryRedisRepository redisRepository) {
        this.jwtProvider = jwtProvider;
        this.redisRepository = redisRepository;
    }

    public void create(ContainerDataCreatedDirectory dataCreatedDirectory) {
        String loginUser = accessClaims.get("login", String.class);
        String roleUser = accessClaims.get("role", String.class);

        DirectoryRedis objRedis = IServiceDirectoryRedis.getObjRedis(
                loginUser,
                roleUser,
                dataCreatedDirectory.nameDirectory(),
                dataCreatedDirectory.pathDirectory().toString()
        );
        push(objRedis);
    }

    public void setAccessToken(String accessToken) {
        setAccessClaims(JwtUtils.getPureAccessToken(accessToken));
    }

    protected void setAccessClaims(String rawAccessToken) {
        this.accessClaims = jwtProvider.getAccessClaims(rawAccessToken);
    }

    @Override
    public void push(DirectoryRedis objRedis) {
        redisRepository.save(objRedis);
    }
}
