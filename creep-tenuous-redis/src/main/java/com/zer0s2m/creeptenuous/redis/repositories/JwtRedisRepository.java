package com.zer0s2m.creeptenuous.redis.repositories;

import com.zer0s2m.creeptenuous.redis.models.JwtRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtRedisRepository extends CrudRepository<JwtRedis, String> { }
