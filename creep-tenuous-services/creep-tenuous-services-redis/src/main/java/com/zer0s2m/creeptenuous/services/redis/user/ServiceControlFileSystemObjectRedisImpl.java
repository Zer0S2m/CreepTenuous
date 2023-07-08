package com.zer0s2m.creeptenuous.services.redis.user;

import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.user.ServiceControlFileSystemObjectRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for controlling file system objects for the user
 */
@Service("service-control-file-system-object-redis")
public class ServiceControlFileSystemObjectRedisImpl implements ServiceControlFileSystemObjectRedis {

    private final FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository;

    @Autowired
    public ServiceControlFileSystemObjectRedisImpl(
            FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository) {
        this.frozenFileSystemObjectRedisRepository = frozenFileSystemObjectRedisRepository;
    }

    @Override
    public void freezingFileSystemObject() {

    }

    @Override
    public void unfreezingFileSystemObject() {

    }

}
