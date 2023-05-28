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
import com.zer0s2m.creeptenuous.services.redis.system.ServiceMoveFileRedisImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = {
        DirectoryRedisRepository.class,
        FileRedisRepository.class,
        JwtProvider.class,
        ServiceMoveFileRedisImpl.class
})
@Transactional
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
                Path.of(systemName).toString()
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
                Path.of(systemName).toString()
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
