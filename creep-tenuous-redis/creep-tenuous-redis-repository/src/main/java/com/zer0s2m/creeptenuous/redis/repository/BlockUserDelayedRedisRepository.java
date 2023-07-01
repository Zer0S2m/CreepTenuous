package com.zer0s2m.creeptenuous.redis.repository;

import com.zer0s2m.creeptenuous.redis.models.BlockUserDelayedRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockUserDelayedRedisRepository extends CrudRepository<BlockUserDelayedRedis, String> {
}
