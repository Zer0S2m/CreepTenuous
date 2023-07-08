package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFile;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCreateFileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceUploadFileRedis;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisManagerRightsAccessImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for loading file system objects and writing to Redis
 */
@Service("service-upload-file-redis")
public class ServiceUploadFileRedisImpl extends BaseServiceFileSystemRedisManagerRightsAccessImpl implements ServiceUploadFileRedis {

    @Autowired
    public ServiceUploadFileRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository,
            JwtProvider jwtProvider) {
        super(directoryRedisRepository, fileRedisRepository, frozenFileSystemObjectRedisRepository,
                jwtProvider);
    }

    /**
     * Push data in redis create file
     * @param dataCreatedFile data upload file
     */
    @Override
    public FileRedis upload(@NotNull ContainerDataUploadFile dataCreatedFile) {
        String loginUser = accessClaims.get("login", String.class);
        String roleUser = accessClaims.get("role", String.class);

        FileRedis objRedis = ServiceCreateFileRedis.getObjRedis(
                loginUser,
                roleUser,
                dataCreatedFile.realNameFile(),
                dataCreatedFile.systemNameFile(),
                dataCreatedFile.systemPathFile().toString(),
                new ArrayList<>()
        );

        push(objRedis);

        return objRedis;
    }

    /**
     * Push data in redis create file
     *
     * @param dataCreatedFile data upload files
     */
    @Override
    public Iterable<FileRedis> upload(@NotNull List<ContainerDataUploadFile> dataCreatedFile) {
        String loginUser = accessClaims.get("login", String.class);
        String roleUser = accessClaims.get("role", String.class);

        List<FileRedis> fileRedisList = dataCreatedFile
                .stream()
                .map(obj -> ServiceCreateFileRedis.getObjRedis(
                        loginUser,
                        roleUser,
                        obj.realNameFile(),
                        obj.systemNameFile(),
                        obj.systemPathFile().toString(),
                        new ArrayList<>()
                ))
                .collect(Collectors.toList());

        return fileRedisRepository.saveAll(fileRedisList);
    }

    /**
     * Push in redis one object
     * @param objRedis must not be {@literal null}.
     */
    @Override
    public void push(FileRedis objRedis) {
        fileRedisRepository.save(objRedis);
    }

}
