package com.zer0s2m.CreepTenuous;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync(proxyTargetClass = true)
public class CreepTenuousApplication {
    public static void main(String[] args) {
        SpringApplication.run(CreepTenuousApplication.class, args);
    }
}
