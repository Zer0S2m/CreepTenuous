package com.zer0s2m.creeptenuous.redis.services.security;

import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.redis.exceptions.NoRightsRedisException;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;

import java.util.List;

/**
 * Service for managing user rights for interacting with a target file system object
 */
public interface ServiceManagerRights {

    /**
     * Checking permissions to perform some action on a specific file object
     * @param operation type of transaction
     * @param fileSystemObjects file system objects
     * @throws NoRightsRedisException Insufficient rights to perform the operation
     */
    void checkRightByOperation(OperationRights operation, List<String> fileSystemObjects) throws NoRightsRedisException;

    /**
     * Set data from access token
     * @param accessToken <b>JWT</b> access token
     */
    void setAccessToken(String accessToken);

    /**
     * Create a user right on a file system object
     * @param right Data right
     * @return right information
     */
    void addRight(RightUserFileSystemObjectRedis right);

    /**
     * Getting login user
     * @return login user
     */
    String getLoginUser();

    /**
     * Get right object to persist in {@literal Redis}
     * @param fileSystemObject file system object name. Must not be {@literal null}.
     * @param login login user. Must not be {@literal null}.
     * @param right list of rights
     * @return {@literal Redis} object
     */
    default RightUserFileSystemObjectRedis buildObj(
            String fileSystemObject,
            String login,
            List<OperationRights> right
    ) {
        if (right.contains(OperationRights.ALL)) {
            return new RightUserFileSystemObjectRedis(fileSystemObject, login, OperationRights.baseOperations());
        }
        return new RightUserFileSystemObjectRedis(fileSystemObject, login, right);
    };

    /**
     * Get right object to persist in {@literal Redis}
     * @param fileSystemObject file system object name. Must not be {@literal null}.
     * @param login login user. Must not be {@literal null}.
     * @param right list of rights
     * @return {@literal Redis} object
     */
    default RightUserFileSystemObjectRedis buildObj(
            String fileSystemObject,
            String login,
            OperationRights right
    ) {
        if (right.equals(OperationRights.ALL)) {
            return buildObj(fileSystemObject, login, OperationRights.baseOperations());
        }
        return buildObj(fileSystemObject, login, List.of(right));
    };
}
