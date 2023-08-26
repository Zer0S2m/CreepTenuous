package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.common.exceptions.ExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.services.redis.resources.ServiceRedisManagerResourcesImpl;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceCheckUniqueNameFileSystemObjectImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceRedis;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest(classes = {
        ServiceCheckUniqueNameFileSystemObjectImpl.class,
        ServiceRedisManagerResourcesImpl.class,
        DirectoryRedisRepository.class,
        FileRedisRepository.class
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceCheckUniqueNameFileSystemObjectTests {

    @Autowired
    private ServiceCheckUniqueNameFileSystemObjectImpl serviceCheckUniqueNameFileSystemObject;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @Test
    public void checkUniqueNameDirectory_success() {
        String systemName = UUID.randomUUID().toString();

        DirectoryRedis directoryRedis1 = directoryRedisRepository.save(new DirectoryRedis(
                "login",
                "role",
                "directory_name_1",
                systemName,
                systemName,
                new ArrayList<>()
        ));
        DirectoryRedis directoryRedis2 = directoryRedisRepository.save(new DirectoryRedis(
                "login_2",
                "role",
                "directory_name_2",
                systemName,
                systemName,
                new ArrayList<>()
        ));

        Assertions.assertDoesNotThrow(
                () -> serviceCheckUniqueNameFileSystemObject.checkUniqueName(
                        "directory_name_2", List.of(systemName), "login"));

        directoryRedisRepository.deleteAll(List.of(directoryRedis1, directoryRedis2));
    }

    @Test
    public void checkUniqueNameFile_success() {
        String systemName = UUID.randomUUID().toString();

        FileRedis fileRedis1 = fileRedisRepository.save(new FileRedis(
                "login",
                "role",
                "file_name_1",
                systemName,
                systemName,
                new ArrayList<>()
        ));
        FileRedis fileRedis2 = fileRedisRepository.save(new FileRedis(
                "login_2",
                "role",
                "file_name_2",
                systemName,
                systemName,
                new ArrayList<>()
        ));

        Assertions.assertDoesNotThrow(
                () -> serviceCheckUniqueNameFileSystemObject.checkUniqueName(
                        "file_name_2", List.of(systemName), "login"));

        fileRedisRepository.deleteAll(List.of(fileRedis1, fileRedis2));
    }

    @Test
    public void checkUniqueName_fail_thereIsSuchFileObject() {
        String systemNameFile = UUID.randomUUID().toString();
        String systemNameDirectory = UUID.randomUUID().toString();

        FileRedis fileRedis = fileRedisRepository.save(new FileRedis(
                "login",
                "role",
                "file_name_1",
                systemNameFile,
                systemNameFile,
                new ArrayList<>()
        ));
        DirectoryRedis directoryRedis = directoryRedisRepository.save(new DirectoryRedis(
                "login",
                "role",
                "directory_name_1",
                systemNameDirectory,
                systemNameDirectory,
                new ArrayList<>()
        ));

        Assertions.assertThrows(
                ExistsFileSystemObjectRedisException.class,
                () -> serviceCheckUniqueNameFileSystemObject.checkUniqueName(
                        "file_name_1", List.of(systemNameFile), "login"));
        Assertions.assertThrows(
                ExistsFileSystemObjectRedisException.class,
                () -> serviceCheckUniqueNameFileSystemObject.checkUniqueName(
                        "directory_name_1", List.of(systemNameDirectory), "login"));

        fileRedisRepository.delete(fileRedis);
        directoryRedisRepository.delete(directoryRedis);
    }

}
