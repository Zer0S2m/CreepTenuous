package com.zer0s2m.creeptenuous.redis.services.user;

/**
 * Interface for implementing control over file system objects for the user
 */
public interface ServiceControlFileSystemObjectRedis {

    void freezingFileSystemObject();

    void unfreezingFileSystemObject();

}
