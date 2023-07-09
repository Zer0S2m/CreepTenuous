package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceMoveDirectoryRedis;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisManagerRightsAccessImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Service for servicing the movement of file system objects in Redis
 */
@Service("service-move-directory-redis")
public class ServiceMoveDirectoryRedisImpl extends BaseServiceFileSystemRedisManagerRightsAccessImpl
        implements ServiceMoveDirectoryRedis {

    /**
     * <b>Key</b> - old source system path<br>
     * <b>Value</b> - new target system path
     */
    private final HashMap<String, String> newPaths = new HashMap<>();

    @Autowired
    public ServiceMoveDirectoryRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository,
            JwtProvider jwtProvider) {
        super(directoryRedisRepository, fileRedisRepository, frozenFileSystemObjectRedisRepository,
                jwtProvider);
    }

    /**
     * Move directories in redis
     * @param attachedSourceSystem info directory from source path
     */
    @Override
    public void move(@NotNull List<ContainerInfoFileSystemObject> attachedSourceSystem) {
        List<String> namesFilesIds = new ArrayList<>();
        List<String> namesDirectoriesIds = new ArrayList<>();

        attachedSourceSystem.forEach((attach) -> {
            newPaths.put(attach.source().toString(), attach.target().toString());
            if (attach.isDirectory()) {
                namesDirectoriesIds.add(attach.nameFileSystemObject());
            } else {
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
