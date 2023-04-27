package com.zer0s2m.CreepTenuous.services.directory.create.services.impl;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.jwt.utils.JwtUtils;
import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;
import com.zer0s2m.CreepTenuous.services.directory.create.services.ICreateDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service("create-directory")
public class ServiceCreateDirectory implements ICreateDirectory {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    private final JwtProvider jwtProvider;

    private final DirectoryRedisRepository redisRepository;

    private Claims accessClaims;

    @Autowired
    public ServiceCreateDirectory(
            ServiceBuildDirectoryPath buildDirectoryPath,
            JwtProvider jwtProvider,
            DirectoryRedisRepository redisRepository
    ) {
        this.buildDirectoryPath = buildDirectoryPath;
        this.jwtProvider = jwtProvider;
        this.redisRepository = redisRepository;
    }

    @Override
    public void create(
            List<String> parents,
            String nameDirectory
    ) throws NoSuchFileException, FileAlreadyExistsException {
        Path path = Paths.get(buildDirectoryPath.build(parents));
        Path pathNewDirectory = Paths.get(path + Directory.SEPARATOR.get() + nameDirectory);

        checkDirectory(pathNewDirectory);

        pathNewDirectory.toFile().mkdir();

        DirectoryRedis objRedis = getObjRedis(accessClaims, nameDirectory, pathNewDirectory.toString());
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
