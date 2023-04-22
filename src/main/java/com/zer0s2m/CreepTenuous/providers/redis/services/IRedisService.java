package com.zer0s2m.CreepTenuous.providers.redis.services;

import com.zer0s2m.CreepTenuous.providers.redis.data.JwtRedisData;
import com.zer0s2m.CreepTenuous.providers.redis.models.JwtRedis;

import java.util.Optional;

public interface IRedisService {
    void save(JwtRedisData data);

    void deleteTokensByLogin(String login);

    Optional<JwtRedis> getByLogin(String login);

    void updateTokens(JwtRedisData data);

    void updateAccessToken(JwtRedisData data);
}
