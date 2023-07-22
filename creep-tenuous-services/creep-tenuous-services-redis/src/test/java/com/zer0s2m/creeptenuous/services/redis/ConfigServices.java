package com.zer0s2m.creeptenuous.services.redis;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableAutoConfiguration
@EntityScan({
        "com.zer0s2m.creeptenuous.models",
        "com.zer0s2m.creeptenuous.redis.models"
})
@EnableRedisRepositories("com.zer0s2m.creeptenuous.redis.repository")
@ComponentScan({
        "com.zer0s2m.creeptenuous.repository"
})
@EnableJpaRepositories("com.zer0s2m.creeptenuous.repository")
public class ConfigServices {
}
