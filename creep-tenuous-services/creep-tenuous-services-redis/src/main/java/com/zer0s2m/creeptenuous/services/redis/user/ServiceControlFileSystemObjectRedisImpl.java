package com.zer0s2m.creeptenuous.services.redis.user;

import com.zer0s2m.creeptenuous.redis.models.FrozenFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.user.ServiceControlFileSystemObjectRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * Freeze a file object by its name
     * @param nameFileSystemObject name file system object
     */
    @Override
    public void freezingFileSystemObject(final String nameFileSystemObject) {
        if (!frozenFileSystemObjectRedisRepository.existsById(nameFileSystemObject)) {
            frozenFileSystemObjectRedisRepository.save(new FrozenFileSystemObjectRedis(
                    nameFileSystemObject));
        }
    }

    /**
     * Unfreeze a file object by its name
     * @param nameFileSystemObject name file system object
     */
    @Override
    public void unfreezingFileSystemObject(final String nameFileSystemObject) {
        frozenFileSystemObjectRedisRepository.deleteAllById(List.of(nameFileSystemObject));
    }

}
