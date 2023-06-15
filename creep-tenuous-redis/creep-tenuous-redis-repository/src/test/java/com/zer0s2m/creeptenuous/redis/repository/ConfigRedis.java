package com.zer0s2m.creeptenuous.redis.repository;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EntityScan({ "com.zer0s2m.creeptenuous.redis.models" })
@EnableRedisRepositories("com.zer0s2m.creeptenuous.redis.repository")
public class ConfigRedis {
}
