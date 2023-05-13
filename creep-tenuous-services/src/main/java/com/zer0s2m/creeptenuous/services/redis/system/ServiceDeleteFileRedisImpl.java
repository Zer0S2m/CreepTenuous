package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDeleteFileRedis;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Optional;

@Service("service-delete-file-redis")
public class ServiceDeleteFileRedisImpl extends BaseServiceFileSystemRedisImpl implements ServiceDeleteFileRedis {
    @Autowired
    public ServiceDeleteFileRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider
    ) {
        super(directoryRedisRepository, fileRedisRepository, jwtProvider);
    }

    /**
     * Delete file from redis
     * @param systemPath system path
     * @param systemNameFile system name file
     */
    @Override
    public void delete(Path systemPath, String systemNameFile) {
        Optional<FileRedis> objRedis = fileRedisRepository.findById(systemNameFile);
        objRedis.ifPresent(this::push);
    }

    @Override
    public void push(FileRedis objRedis) {
        fileRedisRepository.delete(objRedis);
    }
}
