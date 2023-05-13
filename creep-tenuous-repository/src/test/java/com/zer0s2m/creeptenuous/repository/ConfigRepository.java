package com.zer0s2m.creeptenuous.repository;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan({
        "com.zer0s2m.creeptenuous.models",
        "com.zer0s2m.creeptenuous.redis"
})
@EnableJpaRepositories("com.zer0s2m.creeptenuous.repository")
public class ConfigRepository {
}
