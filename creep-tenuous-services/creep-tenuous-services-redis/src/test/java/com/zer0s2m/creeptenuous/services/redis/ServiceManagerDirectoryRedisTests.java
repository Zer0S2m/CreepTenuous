package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.core.services.Distribution;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.starter.test.mock.User;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceManagerDirectoryRedisImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceRedis;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = {
        DirectoryRedisRepository.class,
        FileRedisRepository.class,
        JwtProvider.class,
        ServiceManagerDirectoryRedisImpl.class
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceManagerDirectoryRedisTests {

    @Autowired
    private ServiceManagerDirectoryRedisImpl serviceManagerDirectoryRedis;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @BeforeEach
    void setUp() {
        serviceManagerDirectoryRedis.setAccessToken(UtilsAuthAction.generateAccessToken());
    }

    @Test
    public void build_success() {
        String systemNameDirectory = Distribution.getUUID();
        String systemNameFile = Distribution.getUUID();

        DirectoryRedis directoryRedis = new DirectoryRedis(
                User.LOGIN.get(),
                User.ROLE_USER.get(),
                "test_1",
                systemNameDirectory,
                Path.of(systemNameDirectory).toString(),
                new ArrayList<>()
        );
        directoryRedisRepository.save(directoryRedis);
        FileRedis fileRedis = new FileRedis(
                User.LOGIN.get(),
                User.ROLE_USER.get(),
                "test_1",
                systemNameFile,
                Path.of(systemNameFile).toString(),
                new ArrayList<>()
        );
        fileRedisRepository.save(fileRedis);

        List<Object> dataBuild = serviceManagerDirectoryRedis.build(List.of(systemNameDirectory, systemNameFile));

        Assertions.assertEquals(2, dataBuild.size());

        directoryRedisRepository.delete(directoryRedis);
        fileRedisRepository.delete(fileRedis);
    }

    @Test
    public void buildNotObjects_fail() {
        List<Object> dataBuild = serviceManagerDirectoryRedis.build(new ArrayList<>());
        Assertions.assertEquals(0, dataBuild.size());
    }
}