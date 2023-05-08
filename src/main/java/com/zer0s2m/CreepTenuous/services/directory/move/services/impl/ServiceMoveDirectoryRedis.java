package com.zer0s2m.CreepTenuous.services.directory.move.services.impl;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.FileRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceMoveDirectoryRedis;
import com.zer0s2m.CreepTenuous.services.core.BaseServiceFileSystemRedis;
import com.zer0s2m.CreepTenuous.utils.containers.ContainerInfoFileSystemObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("service-move-directory-redis")
public class ServiceMoveDirectoryRedis extends BaseServiceFileSystemRedis implements IServiceMoveDirectoryRedis {
    /**
     * <b>Key</b> - old source system path<br>
     * <b>Value</b> - new target system path
     */
    private final HashMap<String, String> newPaths = new HashMap<>();

    @Autowired
    public ServiceMoveDirectoryRedis(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider
    ) {
        super(directoryRedisRepository, fileRedisRepository, jwtProvider);
    }

    /**
     * Move directories in redis
     * @param targetSystemPath target path
     * @param sourceSystemPath source path
     * @param attachedSourceSystem info directory from source path
     * @param systemNameDirectory system name directory
     */
    @Override
    public void move(
            Path targetSystemPath,
            Path sourceSystemPath,
            List<ContainerInfoFileSystemObject> attachedSourceSystem,
            String systemNameDirectory
    ) {
        List<String> namesFilesIds = new ArrayList<>();
        List<String> namesDirectoriesIds = new ArrayList<>();

        attachedSourceSystem.forEach((attach) -> {
            newPaths.put(attach.source().toString(), attach.target().toString());
            if (attach.isDirectory()) {
                namesDirectoriesIds.add(attach.nameFileSystemObject());
            } else if (attach.isFile()) {
                namesFilesIds.add(attach.nameFileSystemObject());
            }
        });

        Iterable<FileRedis> fileRedis = fileRedisRepository.findAllById(namesFilesIds);
        Iterable<DirectoryRedis> directoryRedis = directoryRedisRepository.findAllById(namesDirectoriesIds);

        fileRedis.forEach(((objRedis) -> {
            if (newPaths.containsKey(objRedis.getPathFile())) {
                objRedis.setPathFile(newPaths.get(objRedis.getPathFile()));
            }
        }));
        directoryRedis.forEach(((objRedis) -> {
            if (newPaths.containsKey(objRedis.getPathDirectory())) {
                objRedis.setPathDirectory(newPaths.get(objRedis.getPathDirectory()));
            }
        }));

        pushDirectoryRedis(directoryRedis);
        pushFileRedis(fileRedis);
    }

    /**
     * Save data redis directories
     * @param directoryRedisList redis objects
     */
    public void pushDirectoryRedis(Iterable<DirectoryRedis> directoryRedisList) {
        directoryRedisRepository.saveAll(directoryRedisList);
    }

    /**
     * Save data redis files
     * @param fileRedisList redis objects
     */
    public void pushFileRedis(Iterable<FileRedis> fileRedisList) {
        fileRedisRepository.saveAll(fileRedisList);
    }
}
