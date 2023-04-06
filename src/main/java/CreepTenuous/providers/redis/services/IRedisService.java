package CreepTenuous.providers.redis.services;

import CreepTenuous.providers.redis.data.JwtRedisData;
import CreepTenuous.providers.redis.models.JwtRedis;

import java.util.Optional;

public interface IRedisService {
    void save(JwtRedisData data);

    void deleteTokensByLogin(String login);

    Optional<JwtRedis> getByLogin(String login);

    void updateTokens(JwtRedisData data);

    void updateAccessToken(JwtRedisData data);
}
