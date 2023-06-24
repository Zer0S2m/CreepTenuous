package com.zer0s2m.creeptenuous.services.redis.security;

import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceControlUserRights;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ServiceControlUserRightsImpl(DirectoryRedisRepository directoryRedisRepository,
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
