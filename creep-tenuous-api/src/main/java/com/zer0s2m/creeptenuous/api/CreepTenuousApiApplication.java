package com.zer0s2m.creeptenuous.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan({
        "com.zer0s2m.creeptenuous.api",
        "com.zer0s2m.creeptenuous.services.redis",
        "com.zer0s2m.creeptenuous.services.user",
        "com.zer0s2m.creeptenuous.services.system",
        "com.zer0s2m.creeptenuous.services.jwt",
        "com.zer0s2m.creeptenuous.services.security",
        "com.zer0s2m.creeptenuous.common",
        "com.zer0s2m.creeptenuous.redis.models",
        "com.zer0s2m.creeptenuous.redis.repository",
        "com.zer0s2m.creeptenuous.redis.services",
        "com.zer0s2m.creeptenuous.redis.events",
        "com.zer0s2m.creeptenuous.security",
        "com.zer0s2m.creeptenuous.models",
        "com.zer0s2m.creeptenuous.repository",
        "com.zer0s2m.creeptenuous.core",
        "com.zer0s2m.creeptenuous.events",
        "com.zer0s2m.creeptenuous.integration.implants",
})
@EntityScan({
        "com.zer0s2m.creeptenuous.models",
        "com.zer0s2m.creeptenuous.redis.models"
})
@EnableJpaRepositories("com.zer0s2m.creeptenuous.repository")
@EnableRedisRepositories("com.zer0s2m.creeptenuous.redis.repository")
@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource("classpath:integration.properties"),
})
@EnableAsync(proxyTargetClass = true)
public class CreepTenuousApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreepTenuousApiApplication.class, args);
    }

}
