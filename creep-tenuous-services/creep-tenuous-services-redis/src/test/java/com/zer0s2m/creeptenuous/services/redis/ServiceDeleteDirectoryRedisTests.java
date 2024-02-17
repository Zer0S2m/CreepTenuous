package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.security.jwt.JwtProvider;
import com.zer0s2m.creeptenuous.starter.test.mock.User;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceDeleteDirectoryRedisImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceRedis;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;

@SpringBootTest(classes = {
        DirectoryRedisRepository.class,
        FileRedisRepository.class,
        FrozenFileSystemObjectRedisRepository.class,
        JwtProvider.class,
        ServiceDeleteDirectoryRedisImpl.class
})
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
        String systemName = UUID.randomUUID().toString();
        directoryRedisRepository.save(new DirectoryRedis(
                User.LOGIN.get(),
                User.ROLE_USER.get(),
                "test",
                systemName,
                Path.of(systemName).toString(),
                new ArrayList<>()
        ));

        serviceDeleteDirectoryRedis.delete(systemName);

        Assertions.assertFalse(directoryRedisRepository.existsById(systemName));
    }

}
