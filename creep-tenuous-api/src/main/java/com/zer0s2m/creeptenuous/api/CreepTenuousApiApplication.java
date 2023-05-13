package com.zer0s2m.creeptenuous.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan({
        "com.zer0s2m.creeptenuous.api",
        "com.zer0s2m.creeptenuous.services",
        "com.zer0s2m.creeptenuous.common",
        "com.zer0s2m.creeptenuous.redis",
        "com.zer0s2m.creeptenuous.security",
        "com.zer0s2m.creeptenuous.models",
        "com.zer0s2m.creeptenuous.repository"
})
@EntityScan({
        "com.zer0s2m.creeptenuous.models",
        "com.zer0s2m.creeptenuous.redis"
})
@EnableJpaRepositories("com.zer0s2m.creeptenuous.repository")
@EnableRedisRepositories("com.zer0s2m.creeptenuous.redis")
@EnableAsync(proxyTargetClass = true)
public class CreepTenuousApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CreepTenuousApiApplication.class, args);
    }
}
