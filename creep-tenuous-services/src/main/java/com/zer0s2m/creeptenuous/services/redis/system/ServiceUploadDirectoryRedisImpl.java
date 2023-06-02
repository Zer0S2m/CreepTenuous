package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFileSystemObject;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceUploadDirectoryRedis;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("service-upload-directory-redis")
public class ServiceUploadDirectoryRedisImpl extends BaseServiceFileSystemRedisImpl
        implements ServiceUploadDirectoryRedis {
    private String loginUser;

    private String roleUser;

    @Autowired
    public ServiceUploadDirectoryRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider
    ) {
        super(directoryRedisRepository, fileRedisRepository, jwtProvider);
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
    public void upload(List<ContainerDataUploadFileSystemObject> dataUploadFileList) {
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
    private DirectoryRedis buildDirectoryRedis(ContainerDataUploadFileSystemObject dataUploadFile) {
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
    private FileRedis buildFileRedis(ContainerDataUploadFileSystemObject dataUploadFile) {
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
