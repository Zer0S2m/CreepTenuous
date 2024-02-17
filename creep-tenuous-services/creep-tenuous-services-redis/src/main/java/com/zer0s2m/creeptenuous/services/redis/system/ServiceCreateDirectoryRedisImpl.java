package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisManagerRightsAccessImpl;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateDirectory;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCreateDirectoryRedis;
import com.zer0s2m.creeptenuous.security.jwt.JwtProvider;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Service for servicing the creation of file system objects by writing to Redis
 */
@Service("service-directory-redis")
public class ServiceCreateDirectoryRedisImpl extends BaseServiceFileSystemRedisManagerRightsAccessImpl
        implements ServiceCreateDirectoryRedis {

    @Autowired
    public ServiceCreateDirectoryRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository,
            JwtProvider jwtProvider) {
        super(directoryRedisRepository, fileRedisRepository, frozenFileSystemObjectRedisRepository,
                jwtProvider);
    }

    /**
     * Creating a file system object in Redis
     * @param dataCreatedDirectory data to create
     * @return Redis object
     */
    @Override
    public DirectoryRedis create(@NotNull ContainerDataCreateDirectory dataCreatedDirectory) {
        String loginUser = accessClaims.get("login", String.class);
        String roleUser = accessClaims.get("role", String.class);
        return create_(dataCreatedDirectory, loginUser, roleUser);
    }

    /**
     * Creating a file system object in Redis
     * @param dataCreatedDirectory data to create
     * @param login user login
     * @param role user role
     * @return Redis object
     */
    @Override
    public DirectoryRedis create(ContainerDataCreateDirectory dataCreatedDirectory, String login, String role) {
        return create_(dataCreatedDirectory, login, role);
    }

    /**
     * Creating a file system object in Redis
     * @param dataCreatedDirectory data to create
     * @param login user login
     * @param role user role
     * @return Redis object
     */
    private @NotNull DirectoryRedis create_(@NotNull ContainerDataCreateDirectory dataCreatedDirectory,
                                            String login, String role) {
        DirectoryRedis objRedis = ServiceCreateDirectoryRedis.getObjRedis(
                login,
                role,
                dataCreatedDirectory.realNameDirectory(),
                dataCreatedDirectory.systemNameDirectory(),
                dataCreatedDirectory.pathDirectory().toString(),
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
    public void push(DirectoryRedis objRedis) {
        directoryRedisRepository.save(objRedis);
    }

}
