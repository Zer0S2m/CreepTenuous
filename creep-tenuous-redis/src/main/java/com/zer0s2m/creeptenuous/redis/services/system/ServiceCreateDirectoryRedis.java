package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateDirectory;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceRedis;

public interface ServiceCreateDirectoryRedis extends BaseServiceRedis<DirectoryRedis> {
    static DirectoryRedis getObjRedis(
            String login,
            String role,
            String realNameDirectory,
            String systemNameDirectory,
            String pathDirectory
    ) {
        return new DirectoryRedis(login, role, realNameDirectory, systemNameDirectory, pathDirectory);
    }

    DirectoryRedis create(ContainerDataCreateDirectory dataCreatedDirectory);
}
