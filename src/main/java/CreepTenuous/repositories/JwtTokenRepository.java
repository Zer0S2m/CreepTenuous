package CreepTenuous.repositories;

import CreepTenuous.providers.redis.domain.JwtRedisData;

import org.springframework.stereotype.Repository;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import reactor.core.publisher.Mono;

@Repository
public class JwtTokenRepository {
    private final ReactiveRedisOperations<String, JwtRedisData> reactiveRedisOperations;

    public JwtTokenRepository(ReactiveRedisOperations<String, JwtRedisData> reactiveRedisOperations) {
        this.reactiveRedisOperations = reactiveRedisOperations;
    }

    public Mono<Long> save(JwtRedisData dataJwt) {
        return this.reactiveRedisOperations.opsForList().rightPush("jwt-tokens", dataJwt);
    }
}
