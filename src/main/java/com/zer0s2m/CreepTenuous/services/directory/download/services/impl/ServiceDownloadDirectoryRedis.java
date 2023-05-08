package com.zer0s2m.CreepTenuous.services.directory.download.services.impl;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.FileRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceDownloadDirectoryRedis;
import com.zer0s2m.CreepTenuous.services.core.BaseServiceFileSystemRedis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service("service-download-directory-redis")
public class ServiceDownloadDirectoryRedis extends BaseServiceFileSystemRedis
        implements IServiceDownloadDirectoryRedis {
    @Autowired
    public ServiceDownloadDirectoryRedis(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider
    ) {
        super(directoryRedisRepository, fileRedisRepository, jwtProvider);
    }

    /**
     * Get info directory for download directory
     * @param systemPathObject system path object ids {@link DirectoryRedis#getRealNameDirectory()}
     *                         or {@link FileRedis#getRealNameFile()}
     * @return map info directory
     *         <p><b>Key</b> - system name file object</p>
     *         <p><b>Value</b> - real name system file object</p>
     */
    @Override
    public HashMap<String, String> getResource(List<String> systemPathObject) {
        HashMap<String, String> map = new HashMap<>();

        directoryRedisRepository.findAllById(systemPathObject)
                .forEach(objRedis -> map.put(objRedis.getSystemNameDirectory(), objRedis.getRealNameDirectory()));
        fileRedisRepository.findAllById(systemPathObject)
                .forEach(objRedis -> map.put(objRedis.getSystemNameFile(), objRedis.getRealNameFile()));

        return map;
    }
}
