package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCopyFileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCreateFileRedis;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for copying file system objects by writing to Redis
 */
@Service("service-copy-file-redis")
public class ServiceCopyFileRedisImpl extends BaseServiceFileSystemRedisImpl implements ServiceCopyFileRedis {

    @Autowired
    public ServiceCopyFileRedisImpl(DirectoryRedisRepository directoryRedisRepository,
                                    FileRedisRepository fileRedisRepository, JwtProvider jwtProvider) {
        super(directoryRedisRepository, fileRedisRepository, jwtProvider);
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

        if (existingObjsRedis.size() == 0) {
            return new ArrayList<>();
        }

        for (int i = 0; i < newSystemFileName.size(); i++) {
            FileRedis existingObjRedis = existingObjsRedis.get(i);
            FileRedis newObjRedis = ServiceCreateFileRedis.getObjRedis(
                    existingObjRedis.getLogin(),
                    existingObjRedis.getRole(),
                    existingObjRedis.getRealNameFile(),
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
