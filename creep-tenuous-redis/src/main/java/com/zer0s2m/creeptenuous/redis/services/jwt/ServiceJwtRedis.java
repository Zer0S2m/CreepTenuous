package com.zer0s2m.creeptenuous.redis.services.jwt;

import com.zer0s2m.creeptenuous.redis.models.JwtRedis;
import com.zer0s2m.creeptenuous.security.jwt.domain.JwtRedisData;

import java.util.Optional;

public interface ServiceJwtRedis {
    /**
     * Save data tokens in redis
     * @param data data tokens
     */
    void save(JwtRedisData data);

    /**
     * Delete token from redis
     * @param login token
     */
    void deleteTokensByLogin(String login);

    /**
     * Get tokens data by login user
     * @param login login user
     * @return data tokens
     */
    Optional<JwtRedis> getByLogin(String login);

    /**
     * Update data tokens in redis
     * @param data data tokens
     */
    void updateTokens(JwtRedisData data);

    /**
     * Update access token in redis
     * @param data access token
     */
    void updateAccessToken(JwtRedisData data);
}
