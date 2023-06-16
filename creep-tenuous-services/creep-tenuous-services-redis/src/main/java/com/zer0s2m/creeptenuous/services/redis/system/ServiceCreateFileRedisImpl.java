package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateFile;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCreateFileRedis;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisManagerRightsAccessImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Service for servicing the creation of file system objects by writing to Redis
 */
@Service("service-file-redis")
public class ServiceCreateFileRedisImpl extends BaseServiceFileSystemRedisManagerRightsAccessImpl implements ServiceCreateFileRedis {

    @Autowired
    public ServiceCreateFileRedisImpl(DirectoryRedisRepository directoryRedisRepository,
                                      FileRedisRepository fileRedisRepository, JwtProvider jwtProvider) {
        super(directoryRedisRepository, fileRedisRepository, jwtProvider);
    }

    /**
     * Creating a file system object in Redis
     * @param dataCreatedFile data to create
     * @return Redis object
     */
    @Override
    public FileRedis create(@NotNull ContainerDataCreateFile dataCreatedFile) {
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
     * Push in redis one object
     * @param objRedis must not be {@literal null}.
     */
    @Override
    public void push(FileRedis objRedis) {
        fileRedisRepository.save(objRedis);
    }

}
