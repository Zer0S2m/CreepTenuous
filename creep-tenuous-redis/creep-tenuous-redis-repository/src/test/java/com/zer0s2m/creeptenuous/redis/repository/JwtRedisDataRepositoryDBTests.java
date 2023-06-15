package com.zer0s2m.creeptenuous.redis.repository;

import com.zer0s2m.creeptenuous.redis.models.JwtRedis;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.test.context.ContextConfiguration;

@DataRedisTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagRepository
@ContextConfiguration(classes = { ConfigRedis.class })
public class JwtRedisDataRepositoryDBTests {
    Logger logger = LogManager.getLogger(JwtRedisDataRepositoryDBTests.class);

    @Autowired
    private JwtRedisRepository repository;

    JwtRedis RECORD_1 = new JwtRedis(
            "test_login",
            "test_access_token",
            "test_refresh_token"
    );

    @Test
    public void save_success() {
        JwtRedis savedData = repository.save(RECORD_1);
        logger.info("Create tokens in redis: " + savedData);
        Assertions.assertNotNull(savedData);
        repository.deleteById(savedData.getLogin());
    }

    @Test
    public void findById_success() {
        JwtRedis savedData = repository.save(RECORD_1);
        logger.info("Create tokens in redis: " + savedData);
        Assertions.assertNotNull(savedData);
        Assertions.assertNotNull(repository.findById(savedData.getLogin()));
        repository.deleteById(savedData.getLogin());
    }

    @Test
    public void deleteById_success() {
        JwtRedis savedData = repository.save(RECORD_1);
        logger.info("Create tokens in redis: " + savedData);
        Assertions.assertNotNull(savedData);
        repository.deleteById(savedData.getLogin());
        Assertions.assertTrue(repository.findById(savedData.getLogin()).isEmpty());
    }
}
