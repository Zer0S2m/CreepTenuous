package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDeleteDirectoryRedis;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for deleting file system objects from Redis
 */
@Service("delete-directory-redis")
public class ServiceDeleteDirectoryRedisImpl extends BaseServiceFileSystemRedisImpl
        implements ServiceDeleteDirectoryRedis {

    @Autowired
    public ServiceDeleteDirectoryRedisImpl(DirectoryRedisRepository redisRepository,
                                           FileRedisRepository fileRedisRepository, JwtProvider jwtProvider) {
        super(redisRepository, fileRedisRepository, jwtProvider);
    }

    /**
     * Delete object in redis
     * @param namesFileSystemObject system name directory id {@link DirectoryRedis#getRealNameDirectory()}
     */
    @Override
    public void delete(List<String> namesFileSystemObject) {
        directoryRedisRepository.deleteAllById(namesFileSystemObject);
        fileRedisRepository.deleteAllById(namesFileSystemObject);
    }

}
