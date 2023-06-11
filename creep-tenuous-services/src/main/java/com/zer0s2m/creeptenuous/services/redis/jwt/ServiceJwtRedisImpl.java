package com.zer0s2m.creeptenuous.services.redis.jwt;

import com.zer0s2m.creeptenuous.redis.models.JwtRedis;
import com.zer0s2m.creeptenuous.redis.repositories.JwtRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.jwt.ServiceJwtRedis;
import com.zer0s2m.creeptenuous.security.jwt.domain.JwtRedisData;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for maintaining JW tokens in the Redis database
 */
@Service("service-jwt-redis")
@AllArgsConstructor
public class ServiceJwtRedisImpl implements ServiceJwtRedis {

    private final JwtRedisRepository repository;

    /**
     * Save data tokens in redis
     * @param data data tokens
     */
    @Override
    public void save(@NotNull JwtRedisData data) {
        JwtRedis newJwtRedis = new JwtRedis(
                data.login(),
                data.accessToken(),
                data.refreshToken()
        );
        repository.save(newJwtRedis);
    }

    /**
     * Delete token from redis
     * @param login token
     */
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

    /**
     * Get tokens data by login user
     * @param login login user
     * @return data tokens
     */
    @Override
    public Optional<JwtRedis> getByLogin(String login) {
        return repository.findById(login);
    }

    /**
     * Update data tokens in redis
     * @param data data tokens
     */
    @Override
    public void updateTokens(@NotNull JwtRedisData data) {
        Optional<JwtRedis> currentData = getByLogin(data.login());
        if (currentData.isPresent()) {
            JwtRedis readyJwtRedis = currentData.get();
            readyJwtRedis.setRefreshToken(data.refreshToken());
            readyJwtRedis.setAccessToken(data.accessToken());
            repository.save(readyJwtRedis);
        }
    }

    /**
     * Update access token in redis
     * @param data access token
     */
    @Override
    public void updateAccessToken(@NotNull JwtRedisData data) {
        Optional<JwtRedis> currentData = getByLogin(data.login());
        if (currentData.isPresent()) {
            JwtRedis readyJwtRedis = currentData.get();
            readyJwtRedis.setAccessToken(data.accessToken());
            repository.save(readyJwtRedis);
        }
    }

}
