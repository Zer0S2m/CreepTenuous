package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.models.user.UserColor;
import com.zer0s2m.creeptenuous.models.user.UserColorDirectory;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.repository.user.CategoryFileSystemObjectRepository;
import com.zer0s2m.creeptenuous.repository.user.UserColorDirectoryRepository;
import com.zer0s2m.creeptenuous.repository.user.UserColorRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.starter.test.mock.User;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceManagerDirectoryRedisImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceRedis;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest(classes = {
        DirectoryRedisRepository.class,
        FileRedisRepository.class,
        FrozenFileSystemObjectRedisRepository.class,
        UserColorDirectoryRepository.class,
        CategoryFileSystemObjectRepository.class,
        UserRepository.class,
        UserColorRepository.class,
        JwtProvider.class,
        ServiceManagerDirectoryRedisImpl.class
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@Transactional
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceManagerDirectoryRedisTests {

    @Autowired
    private ServiceManagerDirectoryRedisImpl serviceManagerDirectoryRedis;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @Autowired
    private UserColorDirectoryRepository userColorDirectoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserColorRepository userColorRepository;

    @BeforeEach
    void setUp() {
        serviceManagerDirectoryRedis.setAccessToken(UtilsAuthAction.generateAccessToken());
    }

    @Test
    public void build_success() {
        String systemNameDirectory = UUID.randomUUID().toString();
        String systemNameFile = UUID.randomUUID().toString();

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

        List<Object> dataBuild = serviceManagerDirectoryRedis.build(List.of(systemNameDirectory, systemNameFile));

        Assertions.assertEquals(2, dataBuild.size());

        directoryRedisRepository.delete(directoryRedis);
        fileRedisRepository.delete(fileRedis);
    }

    @Test
    @Rollback
    public void build_success_setColorDirectory() {
        String systemNameDirectory = UUID.randomUUID().toString();

        com.zer0s2m.creeptenuous.models.user.User user =
                userRepository.save(new com.zer0s2m.creeptenuous.models.user.User(
                User.LOGIN.get(),
                "password",
                "test_email@email.test",
                "name"
        ));
        UserColor userColor = userColorRepository.save(new UserColor(user, "color"));
        userColorDirectoryRepository.save(new UserColorDirectory(
                user, userColor, UUID.fromString(systemNameDirectory)));

        DirectoryRedis directoryRedis = directoryRedisRepository.save(new DirectoryRedis(
                User.LOGIN.get(),
                User.ROLE_USER.get(),
                "test_1",
                systemNameDirectory,
                Path.of(systemNameDirectory).toString(),
                new ArrayList<>()
        ));

        List<Object> dataBuild = serviceManagerDirectoryRedis.build(List.of(systemNameDirectory));

        Assertions.assertEquals(1, dataBuild.size());

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void buildNotObjects_fail() {
        List<Object> dataBuild = serviceManagerDirectoryRedis.build(new ArrayList<>());
        Assertions.assertEquals(0, dataBuild.size());
    }

}
