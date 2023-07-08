package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.core.services.Distribution;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.starter.test.mock.User;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceMoveFileRedisImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceRedis;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = {
        DirectoryRedisRepository.class,
        FileRedisRepository.class,
        FrozenFileSystemObjectRedisRepository.class,
        JwtProvider.class,
        ServiceMoveFileRedisImpl.class
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceMoveFileRedisTests {

    @Autowired
    private ServiceMoveFileRedisImpl serviceMoveFileRedis;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @BeforeEach
    void setUp() {
        serviceMoveFileRedis.setAccessToken(UtilsAuthAction.generateAccessToken());
    }

    @Test
    public void moveOneFile_success() {
        String systemName = Distribution.getUUID();
        Path newSystemPath = Path.of(Distribution.getUUID(), systemName);
        fileRedisRepository.save(new FileRedis(
                User.LOGIN.get(),
                User.ROLE_USER.get(),
                "test_1",
                systemName,
                Path.of(systemName).toString(),
                new ArrayList<>()
        ));

        Optional<FileRedis> fileRedis = serviceMoveFileRedis.move(newSystemPath, systemName);

        if (fileRedis.isPresent()) {
            FileRedis fileRedisReady = fileRedis.get();
            Assertions.assertEquals(
                    newSystemPath,
                    Path.of(fileRedisReady.getPathFile())
            );
            fileRedisRepository.delete(fileRedisReady);
        }
    }

    @Test
    public void moveMoreOneFile_success() {
        String systemName = Distribution.getUUID();
        Path newSystemPath = Path.of(Distribution.getUUID(), systemName);
        fileRedisRepository.save(new FileRedis(
                User.LOGIN.get(),
                User.ROLE_USER.get(),
                "test_1",
                systemName,
                Path.of(systemName).toString(),
                new ArrayList<>()
        ));

        Iterable<FileRedis> fileRedis = serviceMoveFileRedis.move(newSystemPath, List.of(systemName));

        fileRedis.forEach(obj -> {
            Assertions.assertEquals(
                    newSystemPath,
                    Path.of(obj.getPathFile())
            );
            fileRedisRepository.delete(obj);
        });
    }
}
