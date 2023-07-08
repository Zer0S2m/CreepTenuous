package com.zer0s2m.creeptenuous.redis.services.user;

/**
 * Interface for implementing control over file system objects for the user
 */
public interface ServiceControlFileSystemObjectRedis {

    /**
     * Freeze a file object by its name
     * @param nameFileSystemObject name file system object
     */
    void freezingFileSystemObject(final String nameFileSystemObject);

    /**
     * Unfreeze a file object by its name
     * @param nameFileSystemObject name file system object
     */
    void unfreezingFileSystemObject(final String nameFileSystemObject);

}
