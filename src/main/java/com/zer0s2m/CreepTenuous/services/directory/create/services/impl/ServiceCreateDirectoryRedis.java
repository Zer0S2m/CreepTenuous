package com.zer0s2m.CreepTenuous.services.directory.create.services.impl;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.jwt.utils.JwtUtils;
import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceCreateDirectoryRedis;
import com.zer0s2m.CreepTenuous.services.directory.create.containers.ContainerDataCreatedDirectory;
import com.zer0s2m.CreepTenuous.services.directory.create.exceptions.FileAlreadyExistsException;
import com.zer0s2m.CreepTenuous.services.directory.create.exceptions.NoRightsCreateDirectoryException;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@Service("service-directory-redis")
public class ServiceCreateDirectoryRedis implements IServiceCreateDirectoryRedis {
    private final JwtProvider jwtProvider;

    private final DirectoryRedisRepository redisRepository;

    private Claims accessClaims;

    @Autowired
    public ServiceCreateDirectoryRedis(JwtProvider jwtProvider, DirectoryRedisRepository redisRepository) {
        this.jwtProvider = jwtProvider;
        this.redisRepository = redisRepository;
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

    @Override
    public void checkRights(List<String> parents, List<String> systemParents, String nameDirectory)
            throws NoRightsCreateDirectoryException {
        String loginUser = accessClaims.get("login", String.class);

        Iterable<DirectoryRedis> objsRedis = redisRepository.findAllById(systemParents);

        objsRedis.forEach((objRedis) -> {
            if (!Objects.equals(objRedis.getLogin(), loginUser)) {
                throw new NoRightsCreateDirectoryException();
            }
        });
    }

    @Override
    public void checkIsExistsDirectory(Path systemSource, String nameDirectory) {
        // TODO: finalize
        redisRepository.findAllByRealNameDirectory(nameDirectory).forEach((fileRedis) -> {
            if ((Objects.equals(fileRedis.getRealNameDirectory(), nameDirectory) &&
                    Objects.equals(fileRedis.getPathDirectory(), systemSource.toString()))) {
                throw new FileAlreadyExistsException();
            }
        });
    }
}
