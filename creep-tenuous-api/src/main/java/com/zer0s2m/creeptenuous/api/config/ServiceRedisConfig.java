package com.zer0s2m.creeptenuous.api.config;

import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceRedisConfig {

    @Bean
    public BaseServiceFileSystemRedisImpl baseServiceFileSystemRedis(
            DirectoryRedisRepository directoryRedisRepository, FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider) {
        return new BaseServiceFileSystemRedisImpl(directoryRedisRepository, fileRedisRepository, jwtProvider);
    }
}
