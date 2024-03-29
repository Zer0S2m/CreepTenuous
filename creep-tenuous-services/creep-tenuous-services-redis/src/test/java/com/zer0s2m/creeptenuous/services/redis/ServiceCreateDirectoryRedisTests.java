package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateDirectory;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.security.jwt.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceCreateDirectoryRedisImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceRedis;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.nio.file.Path;
import java.util.UUID;

@SpringBootTest(classes = {
        DirectoryRedisRepository.class,
        FileRedisRepository.class,
        FrozenFileSystemObjectRedisRepository.class,
        JwtProvider.class,
        ServiceCreateDirectoryRedisImpl.class
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceCreateDirectoryRedisTests {

    @Autowired
    private ServiceCreateDirectoryRedisImpl serviceCreateDirectoryRedis;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @BeforeEach
    void setUp() {
        serviceCreateDirectoryRedis.setAccessToken(UtilsAuthAction.generateAccessToken());
    }

    @Test
    public void create_success() {
        String systemName = UUID.randomUUID().toString();
        ContainerDataCreateDirectory data = new ContainerDataCreateDirectory(
                "test",
                systemName,
                Path.of("test")
        );

        DirectoryRedis directoryRedis = serviceCreateDirectoryRedis.create(data);

        Assertions.assertEquals(directoryRedis.getSystemName(), systemName);

        directoryRedisRepository.delete(directoryRedis);
    }

}
