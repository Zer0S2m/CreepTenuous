package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.core.services.Distribution;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.starter.test.mock.User;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceCopyFileRedisImpl;
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
        FrozenFileSystemObjectRedisRepository.class,
        JwtProvider.class,
        ServiceCopyFileRedisImpl.class
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceCopyFileRedisTests {

    @Autowired
    private ServiceCopyFileRedisImpl serviceCopyFileRedis;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @BeforeEach
    void setUp() {
        serviceCopyFileRedis.setAccessToken(UtilsAuthAction.generateAccessToken());
    }

    @Test
    public void copy_success() {
        final String systemName1 = Distribution.getUUID();
        final String newSystemName = Distribution.getUUID();
        final Path path1 = Path.of(systemName1);
        serviceCopyFileRedis.push(new FileRedis(
                User.LOGIN.get(),
                User.ROLE_USER.get(),
                "test_1",
                systemName1,
                path1.toString(),
                new ArrayList<>()
        ));

        List<FileRedis> fileRedisList = serviceCopyFileRedis.copy(
                path1, systemName1, newSystemName
        );

        fileRedisList.forEach(obj -> {
            Assertions.assertEquals(newSystemName, obj.getSystemNameFile());
            fileRedisRepository.delete(obj);
        });
    }

    @Test
    public void copyNotExistsObjects_fail() {
        final String systemName1 = Distribution.getUUID();
        final String newSystemName = Distribution.getUUID();
        final Path path1 = Path.of(systemName1);
        fileRedisRepository.save(new FileRedis(
                User.LOGIN.get(),
                User.ROLE_USER.get(),
                "test",
                systemName1,
                path1.toString(),
                new ArrayList<>()
        ));

        List<FileRedis> fileRedisList = serviceCopyFileRedis.copy(
                path1, Distribution.getUUID(), newSystemName
        );

        Assertions.assertEquals(0, fileRedisList.size());
    }

}
