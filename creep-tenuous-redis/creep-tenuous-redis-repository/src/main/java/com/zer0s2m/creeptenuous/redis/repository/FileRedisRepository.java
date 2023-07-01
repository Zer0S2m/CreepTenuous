package com.zer0s2m.creeptenuous.redis.repository;

import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRedisRepository
        extends CrudRepository<FileRedis, String>, QueryByExampleExecutor<FileRedis> {
}
