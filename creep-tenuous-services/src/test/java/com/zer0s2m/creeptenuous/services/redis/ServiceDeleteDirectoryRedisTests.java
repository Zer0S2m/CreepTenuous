package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.core.services.Distribution;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.ConfigServices;
import com.zer0s2m.creeptenuous.services.helpers.TestTagServiceRedis;
import com.zer0s2m.creeptenuous.services.helpers.User;
import com.zer0s2m.creeptenuous.services.helpers.UtilsAuthAction;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceDeleteDirectoryRedisImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;

@SpringBootTest(classes = {
        DirectoryRedisRepository.class,
        FileRedisRepository.class,
        JwtProvider.class,
        ServiceDeleteDirectoryRedisImpl.class
})
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceDeleteDirectoryRedisTests {

    @Autowired
    private ServiceDeleteDirectoryRedisImpl serviceDeleteDirectoryRedis;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @BeforeEach
    void setUp() {
        serviceDeleteDirectoryRedis.setAccessToken(UtilsAuthAction.generateAccessToken());
    }

    @Test
    public void delete_success() {
        String systemName = Distribution.getUUID();
        directoryRedisRepository.save(new DirectoryRedis(
                User.LOGIN.get(),
                User.ROLE_USER.get(),
                "test",
                systemName,
                Path.of(systemName).toString()
        ));

        serviceDeleteDirectoryRedis.delete(systemName);

        Assertions.assertFalse(directoryRedisRepository.existsById(systemName));
    }
}
