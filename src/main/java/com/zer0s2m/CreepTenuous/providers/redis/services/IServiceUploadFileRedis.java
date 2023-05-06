package com.zer0s2m.CreepTenuous.providers.redis.services;

import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.services.base.IBaseServiceRedis;
import com.zer0s2m.CreepTenuous.services.files.upload.containers.ContainerDataUploadFile;

import java.util.List;

public interface IServiceUploadFileRedis extends IBaseServiceRedis<FileRedis> {
    /**
     * Push data in redis create file
     * @param dataCreatedFile data upload file
     */
    void create(ContainerDataUploadFile dataCreatedFile);

    /**
     * Push data in redis create file
     * @param dataCreatedFile data upload files
     */
    default void create(List<ContainerDataUploadFile> dataCreatedFile) {
        dataCreatedFile.forEach((data) -> {
            if (data != null) {
                create(data);
            }
        });
    }
}
