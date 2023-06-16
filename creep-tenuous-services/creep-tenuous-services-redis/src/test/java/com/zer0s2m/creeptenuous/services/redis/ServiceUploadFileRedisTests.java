package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFile;
import com.zer0s2m.creeptenuous.core.services.Distribution;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceUploadFileRedisImpl;
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

@SpringBootTest(classes = {
        DirectoryRedisRepository.class,
        FileRedisRepository.class,
        JwtProvider.class,
        ServiceUploadFileRedisImpl.class
})
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceUploadFileRedisTests {

    @Autowired
    private ServiceUploadFileRedisImpl serviceUploadFileRedis;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @BeforeEach
    void setUp() {
        serviceUploadFileRedis.setAccessToken(UtilsAuthAction.generateAccessToken());
    }

    @Test
    public void upload_success() {
        String systemName = Distribution.getUUID();
        List<ContainerDataUploadFile> dataUploadFiles = new ArrayList<>();
        dataUploadFiles.add(new ContainerDataUploadFile(
                "test",
                systemName,
                Path.of("test"),
                Path.of(systemName)
        ));

        Iterable<FileRedis> fileRedisList = serviceUploadFileRedis.upload(dataUploadFiles);
        fileRedisList.forEach(obj ->
                Assertions.assertEquals(systemName, obj.getSystemNameFile()));
        fileRedisRepository.deleteAll(fileRedisList);

        FileRedis fileRedis = serviceUploadFileRedis.upload(dataUploadFiles.get(0));
        Assertions.assertEquals(systemName, fileRedis.getSystemNameFile());
        fileRedisRepository.delete(fileRedis);
    }
}
