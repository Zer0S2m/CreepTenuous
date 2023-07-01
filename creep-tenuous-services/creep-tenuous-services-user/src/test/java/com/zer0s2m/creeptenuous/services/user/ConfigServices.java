package com.zer0s2m.creeptenuous.services.user;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableAutoConfiguration
@EntityScan({
        "com.zer0s2m.creeptenuous.models",
        "com.zer0s2m.creeptenuous.redis.models"
})
@EnableJpaRepositories("com.zer0s2m.creeptenuous.repository")
@EnableRedisRepositories("com.zer0s2m.creeptenuous.redis.repository")
public class ConfigServices {
}

