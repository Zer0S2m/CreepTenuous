package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.common.exceptions.ExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCheckUniqueNameFileSystemObject;
import org.springframework.stereotype.Service;

/**
 * Service for checking for uniqueness of the name of the created file object at different directory levels
 */
@Service("service-check-unique-name-file-system-object")
public class ServiceCheckUniqueNameFileSystemObjectImpl implements ServiceCheckUniqueNameFileSystemObject {

    /**
     * Check name for uniqueness depending on directory level
     * @param realName the new name of the object being created
     * @param systemParents parts of the system path - target
     * @throws ExistsFileSystemObjectRedisException uniqueness of the name in the system under
     * different directory levels
     */
    public void checkUniqueName(String realName, Iterable<String> systemParents)
            throws ExistsFileSystemObjectRedisException {

    };

}
