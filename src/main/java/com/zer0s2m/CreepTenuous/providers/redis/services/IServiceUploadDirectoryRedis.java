package com.zer0s2m.CreepTenuous.providers.redis.services;

import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.services.directory.upload.containers.ContainerDataUploadFile;

import java.util.List;

public interface IServiceUploadDirectoryRedis {
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
    void upload(List<ContainerDataUploadFile> dataUploadFileList);

    /**
     * Create objects in redis
     * @param dataUploadFile data upload file system objects
     */
    default void upload(ContainerDataUploadFile dataUploadFile) {
        upload(List.of(dataUploadFile));
    }
}
