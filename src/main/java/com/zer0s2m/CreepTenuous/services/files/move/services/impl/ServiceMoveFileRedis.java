package com.zer0s2m.CreepTenuous.services.files.move.services.impl;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.FileRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceMoveFileRedis;
import com.zer0s2m.CreepTenuous.services.core.BaseServiceFileSystemRedis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Optional;
import java.util.List;

@Service("service-move-file-redis")
public class ServiceMoveFileRedis extends BaseServiceFileSystemRedis implements IServiceMoveFileRedis {
    @Autowired
    public ServiceMoveFileRedis(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider
    ) {
        super(directoryRedisRepository, fileRedisRepository, jwtProvider);
    }

    /**
     * Move file in redis
     * @param systemPath system path file
     * @param systemNameFile system name file
     */
    @Override
    public void move(Path systemPath, String systemNameFile) {
        Optional<FileRedis> objRedis = fileRedisRepository.findById(systemNameFile);

        if (objRedis.isPresent()) {
            FileRedis readyObjRedis = objRedis.get();
            readyObjRedis.setPathFile(systemPath.toString());

            push(readyObjRedis);
        }
    }

    /**
     * Move files in redis
     * @param systemPath system path file
     * @param systemNameFile system names files
     */
    @Override
    public void move(Path systemPath, List<String> systemNameFile) {
        Iterable<FileRedis> objsRedis = fileRedisRepository.findAllById(systemNameFile);
        objsRedis.forEach(objRedis -> objRedis.setPathFile(systemPath.toString()));
        push(objsRedis);
    }

    @Override
    public void push(FileRedis objRedis) {
        fileRedisRepository.save(objRedis);
    }

    public void push(Iterable<FileRedis> entities) {
        fileRedisRepository.saveAll(entities);
    }
}
