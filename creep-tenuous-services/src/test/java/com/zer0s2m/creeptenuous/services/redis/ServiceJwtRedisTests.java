package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.redis.models.JwtRedis;
import com.zer0s2m.creeptenuous.redis.repository.JwtRedisRepository;
import com.zer0s2m.creeptenuous.security.jwt.domain.JwtRedisData;
import com.zer0s2m.creeptenuous.services.ConfigServices;
import com.zer0s2m.creeptenuous.services.redis.jwt.ServiceJwtRedisImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceRedis;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = {
        JwtRedisRepository.class,
        ServiceJwtRedisImpl.class
})
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceJwtRedisTests {
    Logger logger = LogManager.getLogger(ServiceJwtRedisTests.class);

    private ServiceJwtRedisImpl service;

    @Autowired
    private JwtRedisRepository repository;

    @BeforeEach
    void init() {
        service = new ServiceJwtRedisImpl(repository);
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
    @Rollback
    public void save_success() {
        service.save(RECORD_1);
        logger.info("Create tokens in redis: " + RECORD_1);
        Assertions.assertFalse(repository.findById(RECORD_1.login()).isEmpty());
        repository.deleteById(RECORD_1.login());
    }

    @Test
    @Rollback
    public void getByLogin_success() {
        service.save(RECORD_1);
        logger.info("Create tokens in redis: " + RECORD_1);
        Assertions.assertFalse(service.getByLogin(RECORD_1.login()).isEmpty());
        repository.deleteById(RECORD_1.login());
    }

    @Test
    @Rollback
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
    @Rollback
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

    @Test
    public void updateAccessToken_success() {
        service.save(RECORD_1);
        logger.info("Create tokens in redis: " + RECORD_1);
        service.updateAccessToken(RECORD_2);
        Assertions.assertFalse(repository.findById(RECORD_2.login()).isEmpty());
        JwtRedis data = repository.findById(RECORD_2.login()).get();
        Assertions.assertEquals(data.getAccessToken(), RECORD_2.accessToken());
        Assertions.assertEquals(data.getRefreshToken(), RECORD_1.refreshToken());
        repository.deleteById(RECORD_2.login());
    }
}
