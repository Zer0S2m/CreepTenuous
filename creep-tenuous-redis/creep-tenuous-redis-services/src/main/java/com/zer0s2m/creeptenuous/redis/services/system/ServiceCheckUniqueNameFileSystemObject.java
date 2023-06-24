package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.common.exceptions.ExistsFileSystemObjectRedisException;

/**
 * Interface for implementing a check for uniqueness of the name of the created file object at
 * different levels of the directory
 */
public interface ServiceCheckUniqueNameFileSystemObject {

    /**
     * Check name for uniqueness depending on directory level
     * @param realName the new name of the object being created
     * @param systemParents parts of the system path - target
     * @throws ExistsFileSystemObjectRedisException uniqueness of the name in the system under
     * different directory levels
     */
    void checkUniqueName(String realName, Iterable<String> systemParents) throws ExistsFileSystemObjectRedisException;

}
