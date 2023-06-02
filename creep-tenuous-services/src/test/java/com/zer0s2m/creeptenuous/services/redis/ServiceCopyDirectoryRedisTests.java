package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
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
import com.zer0s2m.creeptenuous.services.redis.system.ServiceCopyDirectoryRedisImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
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
        ServiceCopyDirectoryRedisImpl.class
})
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceCopyDirectoryRedisTests {

    @Autowired
    private ServiceCopyDirectoryRedisImpl serviceCopyDirectoryRedis;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @BeforeEach
    void setUp() {
        serviceCopyDirectoryRedis.setAccessToken(UtilsAuthAction.generateAccessToken());
    }

    @Test
    public void copy_success() {
        String newSystemNameDirectory = Distribution.getUUID();
        String newSystemNameFile = Distribution.getUUID();
        final Path pathDirectory = Path.of(newSystemNameDirectory);
        final Path pathFile = Path.of(newSystemNameFile);
        List<ContainerInfoFileSystemObject> data = new ArrayList<>();
        data.add(new ContainerInfoFileSystemObject(
                pathDirectory,
                pathDirectory,
                newSystemNameDirectory,
                false,
                true
        ));
        data.add(new ContainerInfoFileSystemObject(
                pathFile,
                pathFile,
                newSystemNameFile,
                true,
                false
        ));

        directoryRedisRepository.save(new DirectoryRedis(
                User.LOGIN.get(),
                User.ROLE_USER.get(),
                "test_1",
                newSystemNameDirectory,
                pathDirectory.toString(),
                new ArrayList<>()
        ));
        fileRedisRepository.save(new FileRedis(
                User.LOGIN.get(),
                User.ROLE_USER.get(),
                "test_2",
                newSystemNameFile,
                pathFile.toString(),
                new ArrayList<>()
        ));

        serviceCopyDirectoryRedis.copy(data);
    }
}
