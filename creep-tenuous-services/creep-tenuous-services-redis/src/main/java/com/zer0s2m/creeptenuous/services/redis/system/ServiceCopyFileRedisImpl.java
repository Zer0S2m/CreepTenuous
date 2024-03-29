package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCopyFileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCreateFileRedis;
import com.zer0s2m.creeptenuous.security.jwt.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisManagerRightsAccessImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for copying file system objects by writing to Redis
 */
@Service("service-copy-file-redis")
public class ServiceCopyFileRedisImpl extends BaseServiceFileSystemRedisManagerRightsAccessImpl
        implements ServiceCopyFileRedis {

    @Autowired
    public ServiceCopyFileRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository,
            JwtProvider jwtProvider) {
        super(directoryRedisRepository, fileRedisRepository, frozenFileSystemObjectRedisRepository,
                jwtProvider);
    }

    /**
     * Copy files in redis
     * @param target new system directory path
     * @param existingSystemNameFile existing system file names
     * @param newSystemFileName new system file names
     * @return result copy file(s)
     */
    @Override
    public List<FileRedis> copy(List<Path> target, List<String> existingSystemNameFile, List<String> newSystemFileName) {
        List<FileRedis> existingObjsRedis = (List<FileRedis>) fileRedisRepository.findAllById(existingSystemNameFile);
        List<FileRedis> newObjsRedis = new ArrayList<>();

        if (existingObjsRedis.isEmpty()) {
            return new ArrayList<>();
        }

        for (int i = 0; i < newSystemFileName.size(); i++) {
            FileRedis existingObjRedis = existingObjsRedis.get(i);
            FileRedis newObjRedis = ServiceCreateFileRedis.getObjRedis(
                    existingObjRedis.getLogin(),
                    existingObjRedis.getRole(),
                    existingObjRedis.getRealName(),
                    newSystemFileName.get(i),
                    target.get(i).toString(),
                    new ArrayList<>()
            );
            newObjsRedis.add(newObjRedis);
        }

        push(newObjsRedis);

        return newObjsRedis;
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
     * Save all objects to redis
     * @param entities must not be {@literal null} nor must it contain {@literal null} {@link Iterable entities}
     */
    public void push(List<FileRedis> entities) {
        fileRedisRepository.saveAll(entities);
    }

}
