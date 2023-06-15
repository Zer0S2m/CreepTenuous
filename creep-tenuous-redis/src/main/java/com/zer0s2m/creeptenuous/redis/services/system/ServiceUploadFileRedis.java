package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFile;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceRedis;

import java.util.List;

/**
 * Service for loading file system objects and writing to Redis
 */
public interface ServiceUploadFileRedis extends BaseServiceRedis<FileRedis>, BaseServiceFileSystemRedisManagerRightsAccess {

    /**
     * Push data in redis create file
     * @param dataCreatedFile data upload file
     */
    FileRedis upload(ContainerDataUploadFile dataCreatedFile);

    /**
     * Push data in redis create file
     * @param dataCreatedFile data upload files
     */
    Iterable<FileRedis> upload(List<ContainerDataUploadFile> dataCreatedFile);

}
