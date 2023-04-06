package CreepTenuous.providers.redis.repositories;

import CreepTenuous.providers.redis.models.JwtRedis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtRedisDataRepository extends CrudRepository<JwtRedis, String> { }
