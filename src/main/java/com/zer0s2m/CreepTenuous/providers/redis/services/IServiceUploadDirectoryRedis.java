package com.zer0s2m.CreepTenuous.providers.redis.services;

import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.services.directory.upload.containers.ContainerDataUploadFile;

import java.util.List;

public interface IServiceUploadDirectoryRedis {
    void pushDirectoryRedis(List<DirectoryRedis> dataRedis);

    default void pushDirectoryRedis(DirectoryRedis dataRedis) {
        pushDirectoryRedis(List.of(dataRedis));
    }

    void pushFileRedis(List<FileRedis> dataRedis);

    default void pushFileRedis(FileRedis dataRedis) {
        pushFileRedis(List.of(dataRedis));
    }

    void upload(List<ContainerDataUploadFile> dataUploadFileList);

    default void upload(ContainerDataUploadFile dataUploadFile) {
        upload(List.of(dataUploadFile));
    }
}
