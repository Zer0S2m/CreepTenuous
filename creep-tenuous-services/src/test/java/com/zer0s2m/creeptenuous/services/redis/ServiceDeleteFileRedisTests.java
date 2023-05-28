package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.core.services.Distribution;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.ConfigServices;
import com.zer0s2m.creeptenuous.services.helpers.TestTagServiceRedis;
import com.zer0s2m.creeptenuous.services.helpers.User;
import com.zer0s2m.creeptenuous.services.helpers.UtilsAuthAction;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceDeleteFileRedisImpl;
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
        ServiceDeleteFileRedisImpl.class
})
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceDeleteFileRedisTests {

    @Autowired
    private ServiceDeleteFileRedisImpl serviceDeleteFileRedis;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @BeforeEach
    void setUp() {
        serviceDeleteFileRedis.setAccessToken(UtilsAuthAction.generateAccessToken());
    }

    @Test
    public void delete_success() {
        String systemName = Distribution.getUUID();
        final Path path = Path.of(systemName);
        fileRedisRepository.save(new FileRedis(
                User.LOGIN.get(),
                User.ROLE_USER.get(),
                "test",
                systemName,
                path.toString()
        ));

        serviceDeleteFileRedis.delete(path, systemName);

        Assertions.assertFalse(fileRedisRepository.existsById(systemName));
    }
}
