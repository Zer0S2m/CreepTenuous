package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFileSystemObject;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;

import java.util.List;

public interface ServiceUploadDirectoryRedis {
    /**
     * Save data directories
     * @param dataRedis entities must not be {@literal null} nor must it contain {@literal null}.
     */
    void pushDirectoryRedis(Iterable<DirectoryRedis> dataRedis);

    /**
     * Save data directory
     * @param dataRedis entity must not be {@literal null}.
     */
    default void pushDirectoryRedis(DirectoryRedis dataRedis) {
        pushDirectoryRedis(List.of(dataRedis));
    }

    /**
     * Save data files
     * @param dataRedis entities must not be {@literal null} nor must it contain {@literal null}.
     */
    void pushFileRedis(Iterable<FileRedis> dataRedis);

    /**
     * Save data file
     * @param dataRedis entity must not be {@literal null}.
     */
    default void pushFileRedis(FileRedis dataRedis) {
        pushFileRedis(List.of(dataRedis));
    }

    /**
     * Create objects in redis
     * @param dataUploadFileList data upload file system objects
     */
    void upload(List<ContainerDataUploadFileSystemObject> dataUploadFileList);

    /**
     * Create objects in redis
     * @param dataUploadFile data upload file system objects
     */
    default void upload(ContainerDataUploadFileSystemObject dataUploadFile) {
        upload(List.of(dataUploadFile));
    }
}
