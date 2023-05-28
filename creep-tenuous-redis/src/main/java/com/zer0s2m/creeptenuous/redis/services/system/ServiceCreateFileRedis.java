package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateFile;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceRedis;

public interface ServiceCreateFileRedis extends BaseServiceRedis<FileRedis> {
    static FileRedis getObjRedis(
            String login,
            String role,
            String realNameFile,
            String systemNameFile,
            String pathFile
    ) {
        return new FileRedis(login, role, realNameFile, systemNameFile, pathFile);
    }

    FileRedis create(ContainerDataCreateFile dataCreatedFile);
}
