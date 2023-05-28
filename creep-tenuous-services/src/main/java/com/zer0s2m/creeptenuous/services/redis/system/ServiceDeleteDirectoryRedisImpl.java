package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDeleteDirectoryRedis;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("delete-directory-redis")
public class ServiceDeleteDirectoryRedisImpl extends BaseServiceFileSystemRedisImpl
        implements ServiceDeleteDirectoryRedis {
    @Autowired
    public ServiceDeleteDirectoryRedisImpl(
            DirectoryRedisRepository redisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider
    ) {
        super(redisRepository, fileRedisRepository, jwtProvider);
    }

    @Override
    public void push(DirectoryRedis objRedis) {
        directoryRedisRepository.delete(objRedis);
    }

    /**
     * Delete object in redis
     * @param systemNameDirectory system name directory id {@link DirectoryRedis#getRealNameDirectory()}
     */
    @Override
    public void delete(String systemNameDirectory) {
        Optional<DirectoryRedis> objRedis = directoryRedisRepository.findById(systemNameDirectory);
        objRedis.ifPresent(this::push);
    }
}
