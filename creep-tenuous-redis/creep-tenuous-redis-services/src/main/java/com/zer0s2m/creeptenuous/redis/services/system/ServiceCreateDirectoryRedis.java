package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateDirectory;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceRedis;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Service for servicing the creation of file system objects by writing to Redis
 */
public interface ServiceCreateDirectoryRedis extends BaseServiceRedis<DirectoryRedis>,
        BaseServiceFileSystemRedisManagerRightsAccess {

    /**
     * Instantiating a Redis Object Directory
     * @param login user login
     * @param role user role
     * @param realNameDirectory real name directory
     * @param systemNameDirectory system name directory
     * @param pathDirectory source path
     * @param userLogins logins of users who have access to the file object
     * @return Redis object
     */
    @Contract(value = "_, _, _, _, _, _ -> new", pure = true)
    static @NotNull DirectoryRedis getObjRedis(
            String login,
            String role,
            String realNameDirectory,
            String systemNameDirectory,
            String pathDirectory,
            List<String> userLogins
    ) {
        return new DirectoryRedis(login, role, realNameDirectory, systemNameDirectory, pathDirectory, userLogins);
    }

    /**
     * Creating a file system object in Redis
     * @param dataCreatedDirectory data to create
     * @return Redis object
     */
    DirectoryRedis create(ContainerDataCreateDirectory dataCreatedDirectory);

    /**
     * Creating a file system object in Redis
     * @param dataCreatedDirectory data to create
     * @param login user login
     * @param role user role
     * @return Redis object
     */
    DirectoryRedis create(ContainerDataCreateDirectory dataCreatedDirectory, String login, String role);

}
