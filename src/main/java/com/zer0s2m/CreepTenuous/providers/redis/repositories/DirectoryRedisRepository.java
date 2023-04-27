package com.zer0s2m.CreepTenuous.providers.redis.repositories;

import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectoryRedisRepository extends CrudRepository<DirectoryRedis, String> { }
