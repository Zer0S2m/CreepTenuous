package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.common.exceptions.NoExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;

/**
 * Interface to implement renaming of file system objects in Redis
 */
public interface ServiceRenameFileSystemObjectRedis extends BaseServiceFileSystemRedisManagerRightsAccess {

    /**
     * Rename file system object
     * @param systemName filesystem object system name
     * @param newRealName new file object name
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     */
    void rename(String systemName, String newRealName) throws NoExistsFileSystemObjectRedisException;

}
