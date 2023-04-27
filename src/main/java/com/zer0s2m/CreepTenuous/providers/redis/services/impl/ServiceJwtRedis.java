package com.zer0s2m.CreepTenuous.providers.redis.services.impl;

import com.zer0s2m.CreepTenuous.providers.redis.data.JwtRedisData;
import com.zer0s2m.CreepTenuous.providers.redis.models.JwtRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.JwtRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceJwtRedis;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("redis-service")
@AllArgsConstructor
public class ServiceJwtRedis implements IServiceJwtRedis {
    private final JwtRedisRepository repository;

    @Override
    public void save(@NotNull JwtRedisData data) {
        JwtRedis newJwtRedis = new JwtRedis(
                data.login(),
                data.accessToken(),
                data.refreshToken()
        );
        repository.save(newJwtRedis);
    }

    @Override
    public void deleteTokensByLogin(String login) {
        Optional<JwtRedis> currentData = getByLogin(login);
        if (currentData.isPresent()) {
            JwtRedis readyJwtRedis = currentData.get();
            readyJwtRedis.setRefreshToken("");
            readyJwtRedis.setAccessToken("");
            repository.save(readyJwtRedis);
        }
    }

    @Override
    public Optional<JwtRedis> getByLogin(String login) {
        return repository.findById(login);
    }

    @Override
    public void updateTokens(JwtRedisData data) {
        Optional<JwtRedis> currentData = getByLogin(data.login());
        if (currentData.isPresent()) {
            JwtRedis readyJwtRedis = currentData.get();
            readyJwtRedis.setRefreshToken(data.refreshToken());
            readyJwtRedis.setAccessToken(data.accessToken());
            repository.save(readyJwtRedis);
        }
    }

    @Override
    public void updateAccessToken(JwtRedisData data) {
        Optional<JwtRedis> currentData = getByLogin(data.login());
        if (currentData.isPresent()) {
            JwtRedis readyJwtRedis = currentData.get();
            readyJwtRedis.setAccessToken(data.accessToken());
            repository.save(readyJwtRedis);
        }
    }
}
