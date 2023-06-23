package com.zer0s2m.creeptenuous.redis.services.security;

import com.zer0s2m.creeptenuous.common.enums.ManagerRights;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.ChangeRightsYourselfException;
import com.zer0s2m.creeptenuous.common.exceptions.NoExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.common.exceptions.NoExistsRightException;
import com.zer0s2m.creeptenuous.common.exceptions.NoRightsRedisException;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.models.base.BaseRedis;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing user rights for interacting with a target file system object
 */
public interface ServiceManagerRights extends BaseServiceManagerRightsAccess, ServiceManagerRightsExtended {

    /**
     * Separator for creating a unique key (from the system name of the file system object and user login)
     */
    String SEPARATOR_UNIQUE_KEY = ManagerRights.SEPARATOR_UNIQUE_KEY.get();

    /**
     * Checking permissions to perform some action on a specific file object
     * @param operation type of transaction
     * @param fileSystemObjects file system objects
     * @throws NoRightsRedisException Insufficient rights to perform the operation
     */
    void checkRightsByOperation(OperationRights operation, List<String> fileSystemObjects)
            throws NoRightsRedisException;

    /**
     * Checking permissions to perform some action on a specific file object
     * @param operation type of transaction
     * @param fileSystemObjects file system objects
     * @throws NoRightsRedisException Insufficient rights to perform the operation
     */
    default void checkRightsByOperation(OperationRights operation, String fileSystemObjects)
            throws NoRightsRedisException {
        checkRightsByOperation(operation, List.of(fileSystemObjects));
    }

    /**
     * Create a user right on a file system object
     * @param right data right. Must not be {@literal null}.
     * @throws ChangeRightsYourselfException Change rights over the interaction of file system objects to itself
     */
    void addRight(RightUserFileSystemObjectRedis right) throws ChangeRightsYourselfException;

    /**
     * Create a user right on a file system object
     * @param right data right. Must not be {@literal null}.
     * @param operationRights type of transaction. Must not be null.
     * @throws ChangeRightsYourselfException Change rights over the interaction of file system objects to itself
     */
    void addRight(List<RightUserFileSystemObjectRedis> right, OperationRights operationRights)
            throws ChangeRightsYourselfException;

    /**
     * Delete a user right a file system object
     * @param right data right. Must not be {@literal null}.
     * @param operationRights type of transaction. Must not be {@literal null}.
     * @throws ChangeRightsYourselfException Change rights over the interaction of file system objects to itself
     * @throws NoExistsRightException The right was not found in the database.
     *                                Or is {@literal null} {@link NullPointerException}
     */
    void deleteRight(RightUserFileSystemObjectRedis right, OperationRights operationRights)
            throws ChangeRightsYourselfException, NoExistsRightException;

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
     * Checking for deleting rights to itself
     * @param right must not be null.
     * @throws ChangeRightsYourselfException change rights over the interaction of file system objects to itself
     * @throws NoExistsRightException The right was not found in the database.
     *                                Or is {@literal null} {@link NullPointerException}
     */
    void checkDeletingRightsYourself(RightUserFileSystemObjectRedis right)
            throws ChangeRightsYourselfException, NoExistsRightException;

    /**
     * Set setting. Responsible for regulating validation prior to creating or deleting an object.
     * Necessary to avoid exceptions - {@link ChangeRightsYourselfException}.
     * @param isWillBeCreated whether the object will be created
     */
    void setIsWillBeCreated(boolean isWillBeCreated);

    /**
     * Get setting. Responsible for regulating validation prior to creating or deleting an object.
     * @return whether the object will be created
     */
    boolean getIsWillBeCreated();

    /**
     * Get redis object - right
     * @param fileSystemObject  file system object name. Must not be null.
     * @param loginUser login user
     * @return redis object
     */
    RightUserFileSystemObjectRedis getObj(String fileSystemObject, String loginUser);

    /**
     * Owner mapping when moving objects in Redis
     * <p>The current user is taken from the JWT token, Redis services need to know
     * the owner to not overwrite permissions</p>
     * @param loginUser owner user login
     * @param entities must not be {@literal null}. Must not contain {@literal null} elements.
     *                 <p>The class needs a <b>login</b> field and a corresponding <b>getters</b> and <b>setters</b></p>
     * @return objects with a set owner
     */
    List<? extends BaseRedis> ownerMappingOnMove(String loginUser, List<? extends BaseRedis> entities);

    /**
     * Sort file system objects by permissions.
     * @param fileSystemObjects file system objects.
     * @param right data right. Must not be {@literal null}.
     * @return sorted data.
     */
    List<String> permissionFiltering(List<String> fileSystemObjects, OperationRights right);

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
            @NotNull List<OperationRights> right
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
    default RightUserFileSystemObjectRedis buildObj(String fileSystemObject, String login,
                                                    @NotNull OperationRights right) {
        if (right.equals(OperationRights.ALL)) {
            return buildObj(fileSystemObject, login, OperationRights.baseOperations());
        }
        return buildObj(fileSystemObject, login, List.of(right));
    }

    /**
     * Get right objects to persist in {@literal Redis}
     * @param fileSystemObject names file system objects. Must not be {@literal null}.
     * @param login login user. Must not be {@literal null}.
     * @param right list of rights
     * @return {@literal Redis} object
     */
    default List<RightUserFileSystemObjectRedis> buildObj(List<String> fileSystemObject, String login,
                                                          @NotNull OperationRights right) {
        List<RightUserFileSystemObjectRedis> redisList = new ArrayList<>();

        if (right.equals(OperationRights.ALL)) {
            fileSystemObject.forEach(obj -> redisList.add(
                    buildObj(obj, login, OperationRights.baseOperations())));
            return redisList;
        }

        fileSystemObject.forEach(obj -> redisList.add(
                buildObj(obj, login, List.of(right))));

        return redisList;
    }

    /**
     * Creating a unique key from the system name of the file system object and the user login
     * @param systemName the system name of the file system object
     * @param loginUser user login
     * @return generated unique key
     */
    default String buildUniqueKey(String systemName, String loginUser) {
        return systemName + SEPARATOR_UNIQUE_KEY + loginUser;
    }

    /**
     * Unpack a unique name using a delimiter {@link ServiceManagerRights#SEPARATOR_UNIQUE_KEY}
     * @param uniqueName a unique name that was created using a delimiter
     * @return the system name of the file system object
     */
    default String unpackingUniqueKey(@NotNull String uniqueName) {
        return uniqueName.split(SEPARATOR_UNIQUE_KEY)[0];
    }

}
