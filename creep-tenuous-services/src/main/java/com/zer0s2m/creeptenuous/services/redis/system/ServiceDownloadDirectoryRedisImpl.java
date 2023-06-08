package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDownloadDirectoryRedis;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service("service-download-directory-redis")
public class ServiceDownloadDirectoryRedisImpl extends BaseServiceFileSystemRedisImpl
        implements ServiceDownloadDirectoryRedis {
    @Autowired
    public ServiceDownloadDirectoryRedisImpl(
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