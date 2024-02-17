package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.common.exceptions.NoExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceRenameFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.security.jwt.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisManagerRightsAccessImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for renaming file system objects in Redis
 */
@Service("service-rename-file-system-object")
public class ServiceRenameFileSystemObjectRedisImpl extends BaseServiceFileSystemRedisManagerRightsAccessImpl
        implements ServiceRenameFileSystemObjectRedis {

    @Autowired
    public ServiceRenameFileSystemObjectRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository,
            JwtProvider jwtProvider) {
        super(directoryRedisRepository, fileRedisRepository, frozenFileSystemObjectRedisRepository,
                jwtProvider);
    }

    /**
     * Rename file system object
     * @param systemName filesystem object system name
     * @param newRealName new file object name
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     */
    @Override
    public void rename(String systemName, String newRealName) throws NoExistsFileSystemObjectRedisException {
        Optional<FileRedis> fileRedisOptional = fileRedisRepository.findById(systemName);
        Optional<DirectoryRedis> directoryRedisOptional = directoryRedisRepository.findById(systemName);

        if (fileRedisOptional.isEmpty() && directoryRedisOptional.isEmpty()) {
            throw new NoExistsFileSystemObjectRedisException();
        }

        if (fileRedisOptional.isPresent()) {
            FileRedis fileRedis = fileRedisOptional.get();
            fileRedis.setRealName(newRealName);
            fileRedisRepository.save(fileRedis);
        }
        if (directoryRedisOptional.isPresent()) {
            DirectoryRedis directoryRedis = directoryRedisOptional.get();
            directoryRedis.setRealName(newRealName);
            directoryRedisRepository.save(directoryRedis);
        }
    }

}
