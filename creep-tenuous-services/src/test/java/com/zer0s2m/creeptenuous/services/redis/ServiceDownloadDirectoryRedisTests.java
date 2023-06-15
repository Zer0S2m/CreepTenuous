package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.core.services.Distribution;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.ConfigServices;
import com.zer0s2m.creeptenuous.services.helpers.User;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceDownloadDirectoryRedisImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceRedis;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootTest(classes = {
        DirectoryRedisRepository.class,
        FileRedisRepository.class,
        JwtProvider.class,
        ServiceDownloadDirectoryRedisImpl.class
})
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceDownloadDirectoryRedisTests {

    @Autowired
    private ServiceDownloadDirectoryRedisImpl serviceDownloadDirectoryRedis;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @BeforeEach
    void setUp() {
        serviceDownloadDirectoryRedis.setAccessToken(UtilsAuthAction.generateAccessToken());
    }

    @Test
    public void getResource_success() {
        String systemNameDirectory = Distribution.getUUID();
        String systemNameFile = Distribution.getUUID();
        DirectoryRedis directoryRedis = directoryRedisRepository.save(new DirectoryRedis(
                User.LOGIN.get(),
                User.ROLE_USER.get(),
                "test_1",
                systemNameDirectory,
                Path.of(systemNameDirectory).toString(),
                new ArrayList<>()
        ));
        FileRedis fileRedis = fileRedisRepository.save(new FileRedis(
                User.LOGIN.get(),
                User.ROLE_USER.get(),
                "test_1",
                systemNameFile,
                Path.of(systemNameFile).toString(),
                new ArrayList<>()
        ));

        HashMap<String, String> resource = serviceDownloadDirectoryRedis.getResource(
                List.of(systemNameDirectory, systemNameFile)
        );

        Assertions.assertTrue(resource.containsKey(systemNameDirectory));
        Assertions.assertTrue(resource.containsKey(systemNameFile));

        directoryRedisRepository.delete(directoryRedis);
        fileRedisRepository.delete(fileRedis);
    }
}
