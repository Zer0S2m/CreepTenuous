package com.zer0s2m.CreepTenuous.services.files.copy.services.impl;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.FileRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceCopyFileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceCreateFileRedis;
import com.zer0s2m.CreepTenuous.services.core.BaseServiceFileSystemRedis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service("service-copy-file-redis")
public class ServiceCopyFileRedis extends BaseServiceFileSystemRedis implements IServiceCopyFileRedis {
    @Autowired
    public ServiceCopyFileRedis(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider
    ) {
        super(directoryRedisRepository, fileRedisRepository, jwtProvider);
    }

    /**
     * Copy files in redis
     * @param target new system directory path
     * @param existingSystemNameFile existing system file names
     * @param newSystemFileName new system file names
     */
    @Override
    public void copy(List<Path> target, List<String> existingSystemNameFile, List<String> newSystemFileName) {
        List<FileRedis> existingObjsRedis = (List<FileRedis>) fileRedisRepository.findAllById(existingSystemNameFile);
        List<FileRedis> newObjsRedis = new ArrayList<>();

        for (int i = 0; i < newSystemFileName.size(); i++) {
            FileRedis existingObjRedis = existingObjsRedis.get(i);
            FileRedis newObjRedis = IServiceCreateFileRedis.getObjRedis(
                    existingObjRedis.getLogin(),
                    existingObjRedis.getRole(),
                    existingObjRedis.getRealNameFile(),
                    newSystemFileName.get(i),
                    target.get(i).toString()
            );
            newObjsRedis.add(newObjRedis);
        }

        push(newObjsRedis);
    }

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
