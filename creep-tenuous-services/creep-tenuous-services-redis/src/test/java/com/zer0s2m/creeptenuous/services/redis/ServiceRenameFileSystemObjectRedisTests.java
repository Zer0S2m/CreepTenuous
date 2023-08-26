package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.common.exceptions.NoExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceRenameFileSystemObjectRedisImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceRedis;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest(classes = {
        ServiceRenameFileSystemObjectRedisImpl.class,
        DirectoryRedisRepository.class,
        FileRedisRepository.class,
        FrozenFileSystemObjectRedisRepository.class,
        JwtProvider.class
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceRenameFileSystemObjectRedisTests {

    @Autowired
    private ServiceRenameFileSystemObjectRedisImpl serviceRenameFileSystemObjectRedis;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @Test
    public void renameFile_success() {
        String systemName = UUID.randomUUID().toString();

        fileRedisRepository.save(new FileRedis(
                "login",
                "role",
                "name",
                systemName,
                systemName,
                new ArrayList<>()
        ));

        Assertions.assertDoesNotThrow(
                () -> serviceRenameFileSystemObjectRedis.rename(systemName, "new_name"));

        Optional<FileRedis> fileRedisOptional = fileRedisRepository.findById(systemName);
        Assertions.assertTrue(fileRedisOptional.isPresent());
        Assertions.assertEquals("new_name", fileRedisOptional.get().getRealName());

        fileRedisRepository.delete(fileRedisOptional.get());
    }

    @Test
    public void renameDirectory_success() {
        String systemName = UUID.randomUUID().toString();

        directoryRedisRepository.save(new DirectoryRedis(
                "login",
                "role",
                "name",
                systemName,
                systemName,
                new ArrayList<>()
        ));

        Assertions.assertDoesNotThrow(
                () -> serviceRenameFileSystemObjectRedis.rename(systemName, "new_name"));

        Optional<DirectoryRedis> directoryRedisOptional = directoryRedisRepository.findById(systemName);
        Assertions.assertTrue(directoryRedisOptional.isPresent());
        Assertions.assertEquals("new_name", directoryRedisOptional.get().getRealName());

        directoryRedisRepository.delete(directoryRedisOptional.get());
    }

    @Test
    public void rename_fail_notFoundFileObject() {
        Assertions.assertThrows(
                NoExistsFileSystemObjectRedisException.class,
                () -> serviceRenameFileSystemObjectRedis.rename(
                        UUID.randomUUID().toString(), "new_name"));
    }

}
