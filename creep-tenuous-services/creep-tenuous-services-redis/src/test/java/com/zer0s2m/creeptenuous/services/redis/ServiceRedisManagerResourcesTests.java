package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.resources.ServiceRedisManagerResources;
import com.zer0s2m.creeptenuous.services.redis.resources.ServiceRedisManagerResourcesImpl;
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
        DirectoryRedisRepository.class,
        FileRedisRepository.class,
        FrozenFileSystemObjectRedisRepository.class,
        ServiceRedisManagerResourcesImpl.class
})
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

    @Test
    public void getResourceDirectoryRedisByLoginUserAndInSystemNames_success() {
        String systemName = UUID.randomUUID().toString();
        DirectoryRedis directoryRedis1 = new DirectoryRedis("test", "test", "test",
                systemName, systemName, new ArrayList<>());
        DirectoryRedis directoryRedis2 = new DirectoryRedis("test_2", "test", "test",
                "directory_name_2", "directory_name_2", new ArrayList<>());

        directoryRedisRepository.saveAll(List.of(directoryRedis1, directoryRedis2));

        List<DirectoryRedis> directoryRedisList = Assertions.assertDoesNotThrow(
                () -> serviceRedisManagerResources.getResourceDirectoryRedisByLoginUserAndInSystemNames(
                        "test", List.of(UUID.fromString(systemName))));
        Assertions.assertEquals(1, directoryRedisList.size());

        directoryRedisRepository.deleteAll(List.of(directoryRedis1, directoryRedis2));
    }

    @Test
    public void getResourceFileRedisByLoginUserAndInSystemNames_success() {
        String systemName = UUID.randomUUID().toString();
        FileRedis fileRedis1 = new FileRedis("test", "test", "test",
                systemName, systemName, new ArrayList<>());
        FileRedis fileRedis2 = new FileRedis("test_2", "test", "test",
                "file_name_1", "file_name_1", new ArrayList<>());

        fileRedisRepository.saveAll(List.of(fileRedis1, fileRedis2));

        List<FileRedis> fileRedisList = Assertions.assertDoesNotThrow(
                () -> serviceRedisManagerResources.getResourceFileRedisByLoginUserAndInSystemNames(
                        "test", List.of(UUID.fromString(systemName))));
        Assertions.assertEquals(1, fileRedisList.size());

        fileRedisRepository.deleteAll(List.of(fileRedis1, fileRedis2));
    }

    @Test
    public void checkIfFilesRedisExistBySystemNamesAndUserLogin_success() {
        String systemName = UUID.randomUUID().toString();
        DirectoryRedis directoryRedis = new DirectoryRedis("test", "test", "test",
                systemName, systemName, new ArrayList<>());

        directoryRedisRepository.save(directoryRedis);

        long existsCountObjects = Assertions.assertDoesNotThrow(
                () -> serviceRedisManagerResources.checkIfDirectoryRedisExistBySystemNamesAndUserLogin(
                        List.of(systemName), "test"));
        Assertions.assertEquals(1, existsCountObjects);

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void checkIfDirectoryRedisExistBySystemNamesAndUserLogin_success() {
        String systemName = UUID.randomUUID().toString();
        FileRedis fileRedis = new FileRedis("test", "test", "test",
                systemName, systemName, new ArrayList<>());

        fileRedisRepository.save(fileRedis);

        long existsCountObjects = Assertions.assertDoesNotThrow(
                () -> serviceRedisManagerResources.checkIfFilesRedisExistBySystemNamesAndUserLogin(
                        List.of(systemName), "test"));
        Assertions.assertEquals(1, existsCountObjects);

        fileRedisRepository.delete(fileRedis);
    }

    @Test
    public void checkFileObjectDirectoryType_success() {
        String systemName = UUID.randomUUID().toString();
        DirectoryRedis directoryRedis = new DirectoryRedis("test", "test", "test",
                systemName, systemName, new ArrayList<>());

        directoryRedisRepository.save(directoryRedis);

        boolean isExists = Assertions.assertDoesNotThrow(
                () -> serviceRedisManagerResources.checkFileObjectDirectoryType(systemName));
        Assertions.assertTrue(isExists);

        directoryRedisRepository.delete(directoryRedis);
    }

}
