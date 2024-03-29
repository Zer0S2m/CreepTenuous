package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCopyDirectoryRedis;
import com.zer0s2m.creeptenuous.security.jwt.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisManagerRightsAccessImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Service for copying file system objects by writing to Redis
 */
@Service("service-copy-directory-redis")
public class ServiceCopyDirectoryRedisImpl extends BaseServiceFileSystemRedisManagerRightsAccessImpl implements ServiceCopyDirectoryRedis {

    @Autowired
    public ServiceCopyDirectoryRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository,
            JwtProvider jwtProvider) {
        super(directoryRedisRepository, fileRedisRepository, frozenFileSystemObjectRedisRepository,
                jwtProvider);
    }

    /**
     * Copy directory in redis
     * @param attached info directory from source path
     */
    @Override
    public void copy(final @NotNull List<ContainerInfoFileSystemObject> attached) {
        final String loginUser = accessClaims.get("login", String.class);
        final String roleUser = accessClaims.get("role", String.class);

        List<DirectoryRedis> directoryRedisList = new ArrayList<>();
        List<FileRedis> fileRedisList = new ArrayList<>();

        List<String> systemNameDirectorySource = new ArrayList<>();
        List<String> systemNameFileSource = new ArrayList<>();

        attached.forEach((attach) -> {
            if (attach.isDirectory()) {
                systemNameDirectorySource.add(attach.source().getFileName().toString());
            } else {
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
                realNameDirectorySource.put(directoryRedis.getPath(), directoryRedis.getRealName()));
        fileRedisCopyDirectory.forEach((fileRedis ->
                realNameFileSource.put(fileRedis.getPath(), fileRedis.getRealName())));

        attached.forEach((attach) -> {
            if (attach.isDirectory()) {
                directoryRedisList.add(new DirectoryRedis(
                        loginUser,
                        roleUser,
                        realNameDirectorySource.get(attach.source().toString()),
                        attach.nameFileSystemObject(),
                        attach.target().toString(),
                        new ArrayList<>()
                ));
            } else {
                fileRedisList.add(new FileRedis(
                        loginUser,
                        roleUser,
                        realNameFileSource.get(attach.source().toString()),
                        attach.nameFileSystemObject(),
                        attach.target().toString(),
                        new ArrayList<>()
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
