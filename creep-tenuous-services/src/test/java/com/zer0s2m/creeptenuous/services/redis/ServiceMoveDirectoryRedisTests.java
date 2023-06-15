package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataMoveDirectory;
import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.core.services.Distribution;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.ConfigServices;
import com.zer0s2m.creeptenuous.services.helpers.User;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceMoveDirectoryRedisImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceRedis;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = {
        DirectoryRedisRepository.class,
        FileRedisRepository.class,
        JwtProvider.class,
        ServiceMoveDirectoryRedisImpl.class
})
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceMoveDirectoryRedisTests {

    @Autowired
    private ServiceMoveDirectoryRedisImpl serviceMoveDirectoryRedis;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @BeforeEach
    void setUp() {
        serviceMoveDirectoryRedis.setAccessToken(UtilsAuthAction.generateAccessToken());
    }

    @Test
    public void move_success() {
        String systemNameDirectory = Distribution.getUUID();
        String systemNameFile = Distribution.getUUID();
        Path newSystemPath = Path.of(Distribution.getUUID());
        final Path pathSystemDirectory = Path.of(systemNameDirectory);
        final Path pathSystemFile = Path.of(systemNameFile);
        List<ContainerInfoFileSystemObject> attached = new ArrayList<>();

        directoryRedisRepository.save(new DirectoryRedis(
                User.LOGIN.get(),
                User.ROLE_USER.get(),
                "test_1",
                systemNameDirectory,
                pathSystemDirectory.toString(),
                new ArrayList<>()
        ));
        fileRedisRepository.save(new FileRedis(
                User.LOGIN.get(),
                User.ROLE_USER.get(),
                "test_2",
                systemNameFile,
                pathSystemFile.toString(),
                new ArrayList<>()
        ));

        attached.add(new ContainerInfoFileSystemObject(
                pathSystemDirectory,
                newSystemPath,
                systemNameDirectory,
                false,
                true
        ));
        attached.add(new ContainerInfoFileSystemObject(
                pathSystemFile,
                newSystemPath,
                systemNameFile,
                true,
                false
        ));
        ContainerDataMoveDirectory data = new ContainerDataMoveDirectory(
                Path.of("test_1"),
                Path.of("test_2"),
                attached,
                Distribution.getUUID()
        );

        serviceMoveDirectoryRedis.move(data);

        Optional<DirectoryRedis> directoryRedis = directoryRedisRepository.findById(systemNameDirectory);
        Optional<FileRedis> fileRedis = fileRedisRepository.findById(systemNameFile);

        if (directoryRedis.isPresent()) {
            DirectoryRedis directoryRedisReady = directoryRedis.get();
            Assertions.assertEquals(newSystemPath.toString(), directoryRedisReady.getPathDirectory());
            directoryRedisRepository.delete(directoryRedisReady);
        }
        if (fileRedis.isPresent()) {
            FileRedis fileRedisReady = fileRedis.get();
            Assertions.assertEquals(newSystemPath.toString(), fileRedisReady.getPathFile());
            fileRedisRepository.delete(fileRedisReady);
        }
    }
}
