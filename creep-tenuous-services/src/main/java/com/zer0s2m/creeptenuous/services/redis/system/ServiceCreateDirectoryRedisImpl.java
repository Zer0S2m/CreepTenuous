package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisImpl;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateDirectory;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCreateDirectoryRedis;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service("service-directory-redis")
public class ServiceCreateDirectoryRedisImpl extends BaseServiceFileSystemRedisImpl
        implements ServiceCreateDirectoryRedis {
    @Autowired
    public ServiceCreateDirectoryRedisImpl(
            DirectoryRedisRepository redisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider) {
        super(redisRepository, fileRedisRepository, jwtProvider);
    }

    public DirectoryRedis create(ContainerDataCreateDirectory dataCreatedDirectory) {
        String loginUser = accessClaims.get("login", String.class);
        String roleUser = accessClaims.get("role", String.class);

        DirectoryRedis objRedis = ServiceCreateDirectoryRedis.getObjRedis(
                loginUser,
                roleUser,
                dataCreatedDirectory.realNameDirectory(),
                dataCreatedDirectory.systemNameDirectory(),
                dataCreatedDirectory.pathDirectory().toString(),
                new ArrayList<>()
        );
        push(objRedis);
        return objRedis;
    }

    @Override
    public void push(DirectoryRedis objRedis) {
        directoryRedisRepository.save(objRedis);
    }
}
