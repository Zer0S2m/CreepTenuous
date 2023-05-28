package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.core.services.Distribution;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.ConfigServices;
import com.zer0s2m.creeptenuous.services.helpers.TestTagServiceRedis;
import com.zer0s2m.creeptenuous.services.helpers.User;
import com.zer0s2m.creeptenuous.services.helpers.UtilsAuthAction;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceManagerDirectoryRedisImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = {
        DirectoryRedisRepository.class,
        FileRedisRepository.class,
        JwtProvider.class,
        ServiceManagerDirectoryRedisImpl.class
})
@Transactional
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
                Path.of(systemNameDirectory).toString()
        );
        directoryRedisRepository.save(directoryRedis);
        FileRedis fileRedis = new FileRedis(
                User.LOGIN.get(),
                User.ROLE_USER.get(),
                "test_1",
                systemNameFile,
                Path.of(systemNameFile).toString()
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
