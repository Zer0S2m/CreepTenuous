package com.zer0s2m.creeptenuous.redis.repository;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectoryRedisRepository
        extends CrudRepository<DirectoryRedis, String>, QueryByExampleExecutor<DirectoryRedis> {

    List<DirectoryRedis> getDirectoryRedisByLogin(String login);

}
