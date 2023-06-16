package com.zer0s2m.creeptenuous.services.redis;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableAutoConfiguration
@EntityScan({
        "com.zer0s2m.creeptenuous.redis.models"
})
@EnableRedisRepositories("com.zer0s2m.creeptenuous.redis.repository")
public class ConfigServices {
}

