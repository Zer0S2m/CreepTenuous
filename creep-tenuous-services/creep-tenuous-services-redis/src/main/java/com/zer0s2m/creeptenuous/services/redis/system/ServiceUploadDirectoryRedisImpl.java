package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFileSystemObject;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceUploadDirectoryRedis;
import com.zer0s2m.creeptenuous.security.jwt.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisManagerRightsAccessImpl;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for loading file system objects and writing to Redis
 */
@Service("service-upload-directory-redis")
public class ServiceUploadDirectoryRedisImpl extends BaseServiceFileSystemRedisManagerRightsAccessImpl
        implements ServiceUploadDirectoryRedis {

    private String loginUser;

    private String roleUser;

    @Autowired
    public ServiceUploadDirectoryRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository,
            JwtProvider jwtProvider) {
        super(directoryRedisRepository, fileRedisRepository, frozenFileSystemObjectRedisRepository,
                jwtProvider);
    }

    /**
     * Save data directories
     * @param dataRedis entities must not be {@literal null} nor must it contain {@literal null}.
     */
    @Override
    public void pushDirectoryRedis(Iterable<DirectoryRedis> dataRedis) {
        directoryRedisRepository.saveAll(dataRedis);
    }

    /**
     * Save data files
     * @param dataRedis entities must not be {@literal null} nor must it contain {@literal null}.
     */
    @Override
    public void pushFileRedis(Iterable<FileRedis> dataRedis) {
        fileRedisRepository.saveAll(dataRedis);
    }

    /**
     * Create objects in redis
     * @param dataUploadFileList data upload file system objects
     */
    @Override
    public void upload(@NotNull List<ContainerDataUploadFileSystemObject> dataUploadFileList) {
        this.loginUser = accessClaims.get("login", String.class);
        this.roleUser = accessClaims.get("role", String.class);

        List<DirectoryRedis> directoryRedisList = new ArrayList<>();
        List<FileRedis> fileRedisList = new ArrayList<>();

        dataUploadFileList.forEach((objRedis) -> {
            if (objRedis.isDirectory()) {
                directoryRedisList.add(buildDirectoryRedis(objRedis));
            } else {
                fileRedisList.add(buildFileRedis(objRedis));
            }
        });

        pushDirectoryRedis(directoryRedisList);
        pushFileRedis(fileRedisList);
    }

    /**
     * Data preparation for redis
     * @param dataUploadFile data
     * @return object in redis entity
     */
    @Contract("_ -> new")
    private @NotNull DirectoryRedis buildDirectoryRedis(@NotNull ContainerDataUploadFileSystemObject dataUploadFile) {
        return new DirectoryRedis(
                this.loginUser,
                this.roleUser,
                dataUploadFile.realName(),
                dataUploadFile.systemName(),
                dataUploadFile.systemPath().toString(),
                new ArrayList<>()
        );
    }

    /**
     * Data preparation for redis
     * @param dataUploadFile data
     * @return object in redis entity
     */
    @Contract("_ -> new")
    private @NotNull FileRedis buildFileRedis(@NotNull ContainerDataUploadFileSystemObject dataUploadFile) {
        return new FileRedis(
                this.loginUser,
                this.roleUser,
                dataUploadFile.realName(),
                dataUploadFile.systemName(),
                dataUploadFile.systemPath().toString(),
                new ArrayList<>()
        );
    }

}
