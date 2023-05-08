package com.zer0s2m.CreepTenuous.services.directory.copy.services.impl;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.FileRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceCopyDirectoryRedis;
import com.zer0s2m.CreepTenuous.services.core.BaseServiceFileSystemRedis;
import com.zer0s2m.CreepTenuous.utils.containers.ContainerInfoFileSystemObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("service-copy-directory-redis")
public class ServiceCopyDirectoryRedis extends BaseServiceFileSystemRedis implements IServiceCopyDirectoryRedis {
    @Autowired
    public ServiceCopyDirectoryRedis(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider
    ) {
        super(directoryRedisRepository, fileRedisRepository, jwtProvider);
    }

    /**
     * Copy directory in redis
     * @param attached info directory from source path
     */
    @Override
    public void copy(final List<ContainerInfoFileSystemObject> attached) {
        final String loginUser = accessClaims.get("login", String.class);
        final String roleUser = accessClaims.get("role", String.class);

        List<DirectoryRedis> directoryRedisList = new ArrayList<>();
        List<FileRedis> fileRedisList = new ArrayList<>();

        List<String> systemNameDirectorySource = new ArrayList<>();
        List<String> systemNameFileSource = new ArrayList<>();

        attached.forEach((attach) -> {
            System.out.println(attach.source().getFileName().toString());
            if (attach.isDirectory()) {
                systemNameDirectorySource.add(attach.source().getFileName().toString());
            } else if (attach.isFile()) {
                systemNameFileSource.add(attach.source().getFileName().toString());
            }
        });

        Iterable<DirectoryRedis> directoryRedisCopyDirectory = directoryRedisRepository
                .findAllById(systemNameDirectorySource);
        Iterable<FileRedis> fileRedisCopyDirectory = fileRedisRepository
                .findAllById(systemNameFileSource);

        HashMap<String, String> realNameDirectorySource = new HashMap<>();
        HashMap<String, String> realNameFileSource = new HashMap<>();

        directoryRedisCopyDirectory.forEach((directoryRedis) ->
                realNameDirectorySource.put(directoryRedis.getPathDirectory(), directoryRedis.getRealNameDirectory()));
        fileRedisCopyDirectory.forEach((fileRedis ->
                realNameFileSource.put(fileRedis.getPathFile(), fileRedis.getRealNameFile())));

        attached.forEach((attach) -> {
            if (attach.isDirectory()) {
                directoryRedisList.add(new DirectoryRedis(
                        loginUser,
                        roleUser,
                        realNameDirectorySource.get(attach.source().toString()),
                        attach.nameFileSystemObject(),
                        attach.target().toString()
                ));
            } else if (attach.isFile()) {
                fileRedisList.add(new FileRedis(
                        loginUser,
                        roleUser,
                        realNameFileSource.get(attach.source().toString()),
                        attach.nameFileSystemObject(),
                        attach.target().toString()
                ));
            }
        });

        saveAllDirectoryRedis(directoryRedisList);
        saveAllFileRedis(fileRedisList);
    }

    /**
     * Save objects in redis
     * @param entities  must not be {@literal null} nor must it contain {@literal null}
     */
    public void saveAllDirectoryRedis(Iterable<DirectoryRedis> entities) {
        directoryRedisRepository.saveAll(entities);
    }

    /**
     * Save objects in redis
     * @param entities  must not be {@literal null} nor must it contain {@literal null}
     */
    public void saveAllFileRedis(Iterable<FileRedis> entities) {
        fileRedisRepository.saveAll(entities);
    }
}
