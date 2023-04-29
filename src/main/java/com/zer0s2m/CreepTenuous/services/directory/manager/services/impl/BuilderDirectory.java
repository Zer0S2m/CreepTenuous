package com.zer0s2m.CreepTenuous.services.directory.manager.services.impl;

import com.zer0s2m.CreepTenuous.api.controllers.directory.manager.data.DataManagerDirectory;
import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.jwt.utils.JwtUtils;
import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceCreateDirectoryRedis;
import com.zer0s2m.CreepTenuous.services.directory.manager.containers.ContainerDataFiles;
import com.zer0s2m.CreepTenuous.services.core.Directory;
import com.zer0s2m.CreepTenuous.services.directory.manager.exceptions.NotValidLevelDirectoryException;
import com.zer0s2m.CreepTenuous.services.directory.manager.services.IBuilderDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

@Service("builder-ready-directory")
public class BuilderDirectory implements IBuilderDirectory {
    private List<String> arrPartsDirectory;
    
    private final CollectDirectory collectDirectory;

    private final BuilderDataFile builderDataFile;

    private final ServiceBuildDirectoryPath buildDirectoryPath;

    private final DirectoryRedisRepository redisRepository;

    private final JwtProvider jwtProvider;

    private Claims accessClaims;

    @Autowired
    public BuilderDirectory(
            CollectDirectory collectDirectory, 
            BuilderDataFile builderDataFile, 
            ServiceBuildDirectoryPath buildDirectoryPath,
            DirectoryRedisRepository redisRepository,
            JwtProvider jwtProvider
    ) {
        this.collectDirectory = collectDirectory;
        this.builderDataFile = builderDataFile;
        this.buildDirectoryPath = buildDirectoryPath;
        this.redisRepository = redisRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public final String getDirectory() throws NoSuchFileException {
        return buildDirectoryPath.build(this.arrPartsDirectory);
    }

    @Override
    public final List<String> getArrPartsDirectory() {
        return this.arrPartsDirectory;
    }

    @Override
    public DataManagerDirectory build(List<String> arrPartsDirectory, Integer level)
            throws NotValidLevelDirectoryException, IOException, NoSuchFieldException {
        this.arrPartsDirectory = arrPartsDirectory;

        if (level != arrPartsDirectory.toArray().length) {
            throw new NotValidLevelDirectoryException(Directory.NOT_VALID_LEVEL.get());
        }

        String directory = getDirectory();
        ContainerDataFiles data = builderDataFile.build(collectDirectory.collect(directory));

        List<DirectoryRedis> redisList = IServiceCreateDirectoryRedis.getDirectoriesByLogin(
                redisRepository,
                accessClaims.get("login", String.class),
                data.namesDirectory()
        );

        return new DataManagerDirectory(
                getArrPartsDirectory(),
                directory,
                level,
                builderDataFile.build(redisList)
        );
    }

    public void setAccessToken(String rawAccessToken) {
        setAccessClaims(JwtUtils.getPureAccessToken(rawAccessToken));
    }

    protected void setAccessClaims(String accessToken) {
        this.accessClaims = jwtProvider.getAccessClaims(accessToken);
    }
}
