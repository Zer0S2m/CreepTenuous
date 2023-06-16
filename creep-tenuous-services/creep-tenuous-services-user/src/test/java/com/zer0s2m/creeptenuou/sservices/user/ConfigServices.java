package com.zer0s2m.creeptenuou.sservices.user;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableAutoConfiguration
@EntityScan({
        "com.zer0s2m.creeptenuous.models"
})
@EnableJpaRepositories("com.zer0s2m.creeptenuous.repository")
public class ConfigServices {
}

