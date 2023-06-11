package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateFile;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceRedis;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Service for servicing the creation of file system objects by writing to Redis
 */
public interface ServiceCreateFileRedis extends BaseServiceRedis<FileRedis> {

    /**
     * Instantiating a Redis Object File
     * @param login user login
     * @param role user role
     * @param realNameFile real name file
     * @param systemNameFile system name file
     * @param pathFile source path
     * @param userLogins logins of users who have access to the file object
     * @return Redis object
     */
    @Contract(value = "_, _, _, _, _, _ -> new", pure = true)
    static @NotNull FileRedis getObjRedis(
            String login,
            String role,
            String realNameFile,
            String systemNameFile,
            String pathFile,
            List<String> userLogins
    ) {
        return new FileRedis(login, role, realNameFile, systemNameFile, pathFile, userLogins);
    }

    /**
     * Creating a file system object in Redis
     * @param dataCreatedFile data to create
     * @return Redis object
     */
    FileRedis create(ContainerDataCreateFile dataCreatedFile);
}
