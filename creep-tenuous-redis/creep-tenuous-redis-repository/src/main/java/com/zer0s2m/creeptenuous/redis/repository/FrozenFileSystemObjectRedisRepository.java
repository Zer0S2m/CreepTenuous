package com.zer0s2m.creeptenuous.redis.repository;

import com.zer0s2m.creeptenuous.redis.models.FrozenFileSystemObjectRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FrozenFileSystemObjectRedisRepository
        extends CrudRepository<FrozenFileSystemObjectRedis, String> {
}
