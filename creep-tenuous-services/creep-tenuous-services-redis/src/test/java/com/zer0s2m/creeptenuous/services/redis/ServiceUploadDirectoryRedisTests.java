package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFileSystemObject;
import com.zer0s2m.creeptenuous.core.services.Distribution;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceUploadDirectoryRedisImpl;
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
        JwtProvider.class,
        ServiceUploadDirectoryRedisImpl.class
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceUploadDirectoryRedisTests {

    @Autowired
    private ServiceUploadDirectoryRedisImpl serviceUploadDirectoryRedis;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @BeforeEach
    void setUp() {
        serviceUploadDirectoryRedis.setAccessToken(UtilsAuthAction.generateAccessToken());
    }

    @Test
    public void upload_success() {
        String systemNameFile = Distribution.getUUID();
        String systemNameDirectory = Distribution.getUUID();
        List<ContainerDataUploadFileSystemObject> data = new ArrayList<>();
        data.add(new ContainerDataUploadFileSystemObject(
                "test",
                systemNameFile,
                Path.of(systemNameFile),
                true,
                false
        ));
        data.add(new ContainerDataUploadFileSystemObject(
                "test",
                systemNameDirectory,
                Path.of(systemNameDirectory),
                false,
                true
        ));

        serviceUploadDirectoryRedis.upload(data);

        Optional<FileRedis> fileRedis = fileRedisRepository.findById(systemNameFile);
        Optional<DirectoryRedis> directoryRedis = directoryRedisRepository.findById(systemNameDirectory);

        if (fileRedis.isPresent()) {
            Assertions.assertEquals(systemNameFile, fileRedis.get().getSystemNameFile());
            fileRedisRepository.delete(fileRedis.get());
        }

        if (directoryRedis.isPresent()) {
            Assertions.assertEquals(systemNameDirectory, directoryRedis.get().getSystemNameDirectory());
            directoryRedisRepository.delete(directoryRedis.get());
        }
    }
}
