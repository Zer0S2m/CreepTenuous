package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.resources.ServiceRedisManagerResources;
import com.zer0s2m.creeptenuous.services.ConfigServices;
import com.zer0s2m.creeptenuous.services.redis.resources.ServiceRedisManagerResourcesImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceRedis;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = {
        DirectoryRedisRepository.class,
        FileRedisRepository.class,
        ServiceRedisManagerResourcesImpl.class
})
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceRedisManagerResourcesTests {

    @Autowired
    private ServiceRedisManagerResources serviceRedisManagerResources;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Test
    public void getResourcesFilesForMove_success() {
        String systemNameFile = "file_name_1";
        FileRedis fileRedis = new FileRedis("test", "test", "test", systemNameFile,
                systemNameFile, new ArrayList<>());
        fileRedisRepository.save(fileRedis);

        Assertions.assertTrue(serviceRedisManagerResources
                .getResourcesFilesForMove(List.of(systemNameFile)).size() >= 1);
        Assertions.assertTrue(serviceRedisManagerResources
                .getResourcesFilesForMove(systemNameFile).size() >= 1);

        fileRedisRepository.delete(fileRedis);
    }

    @Test
    public void getResourcesDirectoriesForMove_success() {
        String systemDirectoryFile = "directory_name_1";
        DirectoryRedis directoryRedis = new DirectoryRedis("test", "test", "test",
                systemDirectoryFile, systemDirectoryFile, new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        Assertions.assertTrue(serviceRedisManagerResources
                .getResourcesDirectoriesForMove(List.of(systemDirectoryFile)).size() >= 1);
        Assertions.assertTrue(serviceRedisManagerResources
                .getResourcesDirectoriesForMove(systemDirectoryFile).size() >= 1);

        directoryRedisRepository.delete(directoryRedis);
    }

}
