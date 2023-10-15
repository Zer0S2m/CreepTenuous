package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFile;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFileFragment;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceRedis;

import java.util.List;

/**
 * Service for loading file system objects and writing to Redis
 */
public interface ServiceUploadFileRedis extends BaseServiceRedis<FileRedis>, BaseServiceFileSystemRedisManagerRightsAccess {

    /**
     * Push data in redis create file.
     * @param dataCreatedFile Data upload file.
     * @return Created data.
     */
    FileRedis upload(ContainerDataUploadFile dataCreatedFile);

    /**
     * Push data in redis create file.
     * @param dataCreatedFile Data upload files.
     * @return Created data
     */
    Iterable<FileRedis> upload(List<ContainerDataUploadFile> dataCreatedFile);

    /**
     * Push a file to Redis that is fragmented.
     * @param dataUploadFileFragment Fragmented file data.
     * @return Created data.
     */
    FileRedis uploadFragment(ContainerDataUploadFileFragment dataUploadFileFragment);

    /**
     * Push a file to Redis that is fragmented.
     * @param dataUploadFileFragment Fragmented file data.
     * @return Created data.
     */
    Iterable<FileRedis> uploadFragment(List<ContainerDataUploadFileFragment> dataUploadFileFragment);

}
