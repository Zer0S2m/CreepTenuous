package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFile;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceRedis;

import java.util.List;

public interface ServiceUploadFileRedis extends BaseServiceRedis<FileRedis> {
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
