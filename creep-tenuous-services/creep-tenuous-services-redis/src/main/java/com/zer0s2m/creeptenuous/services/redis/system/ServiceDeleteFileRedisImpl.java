package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDeleteFileRedis;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisManagerRightsAccessImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Service for deleting file system objects from Redis
 */
@Service("service-delete-file-redis")
public class ServiceDeleteFileRedisImpl extends BaseServiceFileSystemRedisManagerRightsAccessImpl implements ServiceDeleteFileRedis {

    @Autowired
    public ServiceDeleteFileRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository,
            JwtProvider jwtProvider) {
        super(directoryRedisRepository, fileRedisRepository, frozenFileSystemObjectRedisRepository,
                jwtProvider);
    }

    /**
     * Delete file from redis
     * @param systemPath system path
     * @param systemNameFile system name file
     */
    @Override
    public void delete(Path systemPath, String systemNameFile) {
        Optional<FileRedis> objRedis = fileRedisRepository.findById(systemNameFile);
        objRedis.ifPresent(this::push);
    }

    /**
     * Push in redis one object
     * @param objRedis must not be {@literal null}.
     */
    @Override
    public void push(FileRedis objRedis) {
        fileRedisRepository.delete(objRedis);
    }

}
