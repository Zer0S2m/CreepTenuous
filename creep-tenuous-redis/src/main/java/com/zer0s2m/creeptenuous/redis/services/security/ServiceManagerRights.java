package com.zer0s2m.creeptenuous.redis.services.security;

import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.redis.exceptions.ChangeRightsYourselfException;
import com.zer0s2m.creeptenuous.redis.exceptions.NoExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.redis.exceptions.NoRightsRedisException;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import io.jsonwebtoken.Claims;

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
     * @throws ChangeRightsYourselfException change rights over the interaction of file system objects to itself
     */
    void addRight(RightUserFileSystemObjectRedis right) throws ChangeRightsYourselfException;

    /**
     * Set access claims (resources), from raw access token
     * @param rawAccessToken <b>JWT</b> raw access token, example (string):
     * <pre>
     * Bearer: token...
     * </pre>
     */
    void setAccessClaims(String rawAccessToken);

    /**
     * Set access claims (resources)
     * @param accessClaims This is ultimately a JSON map and any values can be added to it, but JWT standard
     *                     names are provided as type-safe getters and setters for convenience.
     */
    void setAccessClaims(Claims accessClaims);

    /**
     * Checking for the existence of a file system object in the database
     * @param nameFileSystemObject The system name of the file system object
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     */
    void isExistsFileSystemObject(String nameFileSystemObject) throws NoExistsFileSystemObjectRedisException;

    /**
     * Checking for the existence of a user
     * @param loginUser login user
     * @throws UserNotFoundException the user does not exist in the system
     */
    void isExistsUser(String loginUser) throws UserNotFoundException;

    /**
     * Checking for adding rights to itself
     * @param right must not be null.
     * @throws ChangeRightsYourselfException change rights over the interaction of file system objects to itself
     */
    void checkAddingRightsYourself(RightUserFileSystemObjectRedis right) throws ChangeRightsYourselfException;

    /**
     * Getting login user
     * @return login user
     */
    String getLoginUser();

    /**
     * Setting login user
     */
    void setLoginUser(String loginUser);

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
    }

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
    }

}
