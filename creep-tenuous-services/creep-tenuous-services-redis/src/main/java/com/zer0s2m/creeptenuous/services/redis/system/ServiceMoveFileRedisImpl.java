package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceMoveFileRedis;
import com.zer0s2m.creeptenuous.security.jwt.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisManagerRightsAccessImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Optional;
import java.util.List;

/**
 * Service for servicing the movement of file system objects in Redis
 */
@Service("service-move-file-redis")
public class ServiceMoveFileRedisImpl extends BaseServiceFileSystemRedisManagerRightsAccessImpl implements ServiceMoveFileRedis {

    @Autowired
    public ServiceMoveFileRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository,
            JwtProvider jwtProvider) {
        super(directoryRedisRepository, fileRedisRepository, frozenFileSystemObjectRedisRepository,
                jwtProvider);
    }

    /**
     * Move file in redis
     * @param systemPath system path file
     * @param systemNameFile system name file
     * @return result move file(s)
     */
    @Override
    public Optional<FileRedis> move(Path systemPath, String systemNameFile) {
        Optional<FileRedis> objRedis = fileRedisRepository.findById(systemNameFile);

        if (objRedis.isPresent()) {
            FileRedis readyObjRedis = objRedis.get();
            readyObjRedis.setPath(systemPath.toString());

            push(readyObjRedis);
        }

        return objRedis;
    }

    /**
     * Move files in redis
     * @param systemPath system path file
     * @param systemNameFile system names files
     * @return result move file(s)
     */
    @Override
    public Iterable<FileRedis> move(Path systemPath, List<String> systemNameFile) {
        Iterable<FileRedis> objsRedis = fileRedisRepository.findAllById(systemNameFile);
        objsRedis.forEach(objRedis -> objRedis.setPath(systemPath.toString()));
        push(objsRedis);
        return objsRedis;
    }

    /**
     * Push in redis one object
     * @param objRedis must not be {@literal null}.
     */
    @Override
    public void push(FileRedis objRedis) {
        fileRedisRepository.save(objRedis);
    }

    /**
     * Push in redis one object
     * @param entities must not be {@literal null}.
     */
    public void push(Iterable<FileRedis> entities) {
        fileRedisRepository.saveAll(entities);
    }

}
