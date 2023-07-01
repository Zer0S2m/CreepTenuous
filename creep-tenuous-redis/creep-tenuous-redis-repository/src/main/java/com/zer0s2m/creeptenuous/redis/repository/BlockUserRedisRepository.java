package com.zer0s2m.creeptenuous.redis.repository;

import com.zer0s2m.creeptenuous.redis.models.BlockUserRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockUserRedisRepository extends CrudRepository<BlockUserRedis, String> {
}
