package com.zer0s2m.creeptenuous.redis.repository;

import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.FluentQuery;

import java.util.function.Function;

/**
 * Interface for generic CRUD operations on a repository for {@link RightUserFileSystemObjectRedis}.
 */
public interface RightUserFileSystemObjectRedisRepository
        extends CrudRepository<RightUserFileSystemObjectRedis, String> {
}
