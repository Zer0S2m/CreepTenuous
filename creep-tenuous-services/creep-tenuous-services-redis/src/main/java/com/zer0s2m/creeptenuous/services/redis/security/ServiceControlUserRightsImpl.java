package com.zer0s2m.creeptenuous.services.redis.security;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceControlUserRights;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

/**
 * Service for controlling user rights over file system objects, such as:
 * <ul>
 *     <li>Deleting a user</li>
 * </ul>
 */
@Service("service-control-user-rights")
public class ServiceControlUserRightsImpl implements ServiceControlUserRights {

    private final DirectoryRedisRepository directoryRedisRepository;

    private final FileRedisRepository fileRedisRepository;

    @Autowired
    public ServiceControlUserRightsImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository) {
        this.directoryRedisRepository = directoryRedisRepository;
        this.fileRedisRepository = fileRedisRepository;
    }

    /**
     * remove filesystem objects from redis
     * @param userLogin user login
     */
    @Override
    public void removeFileSystemObjects(String userLogin) {
        DirectoryRedis directoryRedisExample = new DirectoryRedis();
        directoryRedisExample.setLogin(userLogin);
        FileRedis fileRedisExample = new FileRedis();
        fileRedisExample.setLogin(userLogin);

        Iterable<DirectoryRedis> directoryRedisList = directoryRedisRepository
                .findAll(Example.of(directoryRedisExample));
        Iterable<FileRedis> fileRedisList = fileRedisRepository
                .findAll(Example.of(fileRedisExample));

        directoryRedisRepository.deleteAll(directoryRedisList);
        fileRedisRepository.deleteAll(fileRedisList);
    }

    /**
     * Remove granted permissions for user
     * @param userLogin user login
     */
    @Override
    public void removeGrantedPermissionsForUser(String userLogin) {

    }

    /**
     * Remove assigned permissions for user
     * @param userLogin user login
     */
    @Override
    public void removeAssignedPermissionsForUser(String userLogin) {

    }

}
