package com.zer0s2m.creeptenuous.redis;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EntityScan({ "com.zer0s2m.creeptenuous.redis" })
@EnableRedisRepositories("com.zer0s2m.creeptenuous.redis")
public class ConfigRedis {
}
