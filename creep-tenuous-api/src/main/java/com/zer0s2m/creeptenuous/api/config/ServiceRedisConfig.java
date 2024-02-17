package com.zer0s2m.creeptenuous.api.config;

import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.security.jwt.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisManagerRightsAccessImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceRedisConfig {

    @Bean
    public BaseServiceFileSystemRedisManagerRightsAccessImpl baseServiceFileSystemRedis(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository,
            JwtProvider jwtProvider) {
        return new BaseServiceFileSystemRedisManagerRightsAccessImpl(
                directoryRedisRepository,
                fileRedisRepository,
                frozenFileSystemObjectRedisRepository,
                jwtProvider);
    }

}
