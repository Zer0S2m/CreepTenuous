package CreepTenuous.providers.redis.services.imple;

import CreepTenuous.providers.redis.data.JwtRedisData;
import CreepTenuous.providers.redis.models.JwtRedis;
import CreepTenuous.providers.redis.services.IRedisService;
import CreepTenuous.providers.redis.repositories.JwtRedisDataRepository;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("redis-service")
@AllArgsConstructor
public class RedisService implements IRedisService {
    private final JwtRedisDataRepository repository;

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
