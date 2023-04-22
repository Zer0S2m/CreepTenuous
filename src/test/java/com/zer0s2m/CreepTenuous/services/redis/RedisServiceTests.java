package com.zer0s2m.CreepTenuous.services.redis;

import com.zer0s2m.CreepTenuous.providers.redis.data.JwtRedisData;
import com.zer0s2m.CreepTenuous.providers.redis.models.JwtRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.JwtRedisDataRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.imple.RedisService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

@DataRedisTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class RedisServiceTests {
    Logger logger = LogManager.getLogger(RedisServiceTests.class);

    private RedisService service;

    @Autowired
    private JwtRedisDataRepository repository;

    @BeforeEach
    void init() {
        service = new RedisService(repository);
    }

    JwtRedisData RECORD_1 = new JwtRedisData(
            "test_login",
            "test_access_token",
            "test_refresh_token"
    );

    JwtRedisData RECORD_2 = new JwtRedisData(
            "test_login",
            "test_access_token_2",
            "test_refresh_token_2"
    );

    @Test
    public void save_success() {
        service.save(RECORD_1);
        logger.info("Create tokens in redis: " + RECORD_1);
        Assertions.assertFalse(repository.findById(RECORD_1.login()).isEmpty());
        repository.deleteById(RECORD_1.login());
    }

    @Test
    public void getByLogin_success() {
        service.save(RECORD_1);
        logger.info("Create tokens in redis: " + RECORD_1);
        Assertions.assertFalse(service.getByLogin(RECORD_1.login()).isEmpty());
        repository.deleteById(RECORD_1.login());
    }

    @Test
    public void updateTokens_success() {
        service.save(RECORD_1);
        logger.info("Create tokens in redis: " + RECORD_1);
        service.updateTokens(RECORD_2);
        Assertions.assertFalse(repository.findById(RECORD_2.login()).isEmpty());
        JwtRedis data = repository.findById(RECORD_2.login()).get();
        Assertions.assertEquals(data.getAccessToken(), RECORD_2.accessToken());
        Assertions.assertEquals(data.getRefreshToken(), RECORD_2.refreshToken());
        repository.deleteById(RECORD_2.login());
    }

    @Test
    public void deleteTokensByLogin_success() {
        service.save(RECORD_1);
        logger.info("Create tokens in redis: " + RECORD_1);
        service.deleteTokensByLogin(RECORD_1.login());
        Assertions.assertFalse(service.getByLogin(RECORD_1.login()).isEmpty());
        JwtRedis data = repository.findById(RECORD_2.login()).get();
        Assertions.assertEquals(data.getAccessToken(), "");
        Assertions.assertEquals(data.getRefreshToken(), "");
        repository.deleteById(RECORD_1.login());
    }
}
