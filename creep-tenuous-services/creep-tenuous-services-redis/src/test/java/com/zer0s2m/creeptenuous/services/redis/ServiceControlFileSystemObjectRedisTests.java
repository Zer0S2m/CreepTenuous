package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.redis.models.FrozenFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.user.ServiceControlFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.services.redis.user.ServiceControlFileSystemObjectRedisImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceRedis;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

@SpringBootTest(classes = {
        FrozenFileSystemObjectRedisRepository.class,
        ServiceControlFileSystemObjectRedisImpl.class
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceControlFileSystemObjectRedisTests {

    @Autowired
    private ServiceControlFileSystemObjectRedis serviceControlFileSystemObjectRedis;

    @Autowired
    private FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository;

    @Test
    public void freezingFileSystemObject_success() {
        final String systemName = UUID.randomUUID().toString();

        Assertions.assertDoesNotThrow(
                () -> serviceControlFileSystemObjectRedis.freezingFileSystemObject(systemName)
        );

        Assertions.assertTrue(frozenFileSystemObjectRedisRepository.existsById(systemName));
        frozenFileSystemObjectRedisRepository.deleteById(systemName);
    }

    @Test
    public void freezingFileSystemObject_success_exists() {
        final String systemName = UUID.randomUUID().toString();
        frozenFileSystemObjectRedisRepository.save(new FrozenFileSystemObjectRedis(
                systemName));

        Assertions.assertDoesNotThrow(
                () -> serviceControlFileSystemObjectRedis.freezingFileSystemObject(systemName)
        );

        Assertions.assertTrue(frozenFileSystemObjectRedisRepository.existsById(systemName));
        frozenFileSystemObjectRedisRepository.deleteById(systemName);
    }

    @Test
    public void unfreezingFileSystemObject_success() {
        final String systemName = UUID.randomUUID().toString();

        Assertions.assertDoesNotThrow(
                () -> serviceControlFileSystemObjectRedis.unfreezingFileSystemObject(systemName)
        );

        Assertions.assertFalse(frozenFileSystemObjectRedisRepository.existsById(systemName));
    }

}
