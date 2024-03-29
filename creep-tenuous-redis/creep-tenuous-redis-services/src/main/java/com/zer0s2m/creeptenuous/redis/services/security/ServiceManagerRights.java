package com.zer0s2m.creeptenuous.redis.services.security;

import com.zer0s2m.creeptenuous.common.containers.ContainerGrantedRight;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.ChangeRightsYourselfException;
import com.zer0s2m.creeptenuous.common.exceptions.NoExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.common.exceptions.NoExistsRightException;
import com.zer0s2m.creeptenuous.common.exceptions.NoRightsRedisException;
import com.zer0s2m.creeptenuous.common.containers.ContainerAssignedRights;
import com.zer0s2m.creeptenuous.common.http.ResponseGrantedRightsApi;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.models.base.BaseRedis;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Service for managing user rights for interacting with a target file system object
 */
public interface ServiceManagerRights extends BaseServiceManagerRightsAccess,
        ServiceManagerRightsExtended, CollectUniqueKeyRightUserFileObject {

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
     * @param right data right. must not be {@literal null} nor must it contain {@literal null}.
     * @param operationRights type of transaction. Must not be null.
     * @throws ChangeRightsYourselfException Change rights over the interaction of file system objects to itself
     */
    default void addRight(List<RightUserFileSystemObjectRedis> right, OperationRights operationRights)
            throws ChangeRightsYourselfException {
        addRight(right, List.of(operationRights));
    }

    /**
     * Create a user right on a file system object
     * @param right data right. must not be {@literal null} nor must it contain {@literal null}.
     * @param operationRights type of transaction. Must not be null.
     * @throws ChangeRightsYourselfException Change rights over the interaction of file system objects to itself
     */
    void addRight(List<RightUserFileSystemObjectRedis> right, List<OperationRights> operationRights)
            throws ChangeRightsYourselfException;

    /**
     * set directory pass access if the nesting level prevents the user from reaching the file system object
     * @param nameFileSystemObject filesystem object system name. Must not be {@literal null}.
     * @param loginUser login user. Must not be {@literal null}.
     * @throws ChangeRightsYourselfException Change rights over the interaction of file system objects to itself
     */
    void setDirectoryPassAccess(String nameFileSystemObject, String loginUser) throws ChangeRightsYourselfException;

    /**
     * Delete a user right a file system object
     * @param right data right. Must not be {@literal null}.
     * @param operationRights type of transaction. Must not be {@literal null}.
     * @throws ChangeRightsYourselfException Change rights over the interaction of file system objects to itself
     * @throws NoExistsRightException The right was not found in the database.
     *                                Or is {@literal null} {@link NullPointerException}
     */
    default void deleteRight(RightUserFileSystemObjectRedis right, OperationRights operationRights)
            throws ChangeRightsYourselfException, NoExistsRightException {
        deleteRight(right, List.of(operationRights));
    }

    /**
     * Delete a user right a file system object
     * @param right data right. Must not be {@literal null}.
     * @param operationRights type of transaction. Must not be {@literal null}.
     * @throws ChangeRightsYourselfException Change rights over the interaction of file system objects to itself
     * @throws NoExistsRightException The right was not found in the database.
     *                                Or is {@literal null} {@link NullPointerException}
     */
    void deleteRight(RightUserFileSystemObjectRedis right, List<OperationRights> operationRights)
            throws ChangeRightsYourselfException, NoExistsRightException;

    /**
     * Delete a user rights a file system object
     * @param right data right. must not be {@literal null} nor must it contain {@literal null}.
     * @param operationRights type of transaction. Must not be null.
     * @throws ChangeRightsYourselfException Change rights over the interaction of file system objects to itself
     * @throws NoExistsRightException The right was not found in the database.
     *                                Or is {@literal null} {@link NullPointerException}
     */
    default void deleteRight(List<RightUserFileSystemObjectRedis> right, OperationRights operationRights)
            throws ChangeRightsYourselfException, NoExistsRightException {
        deleteRight(right, List.of(operationRights));
    }

    /**
     * Delete a user rights a file system object
     * @param right data right. must not be {@literal null} nor must it contain {@literal null}.
     * @param operationRights type of transaction. Must not be null.
     * @throws ChangeRightsYourselfException Change rights over the interaction of file system objects to itself
     * @throws NoExistsRightException The right was not found in the database.
     *                                Or is {@literal null} {@link NullPointerException}
     */
    void deleteRight(List<RightUserFileSystemObjectRedis> right, List<OperationRights> operationRights)
            throws ChangeRightsYourselfException, NoExistsRightException;

    /**
     * Get all granted rights to the specified object
     * @param systemName file system object
     * @return granted rights
     */
    List<ContainerGrantedRight> getGrantedRight(final String systemName);

    /**
     * Get information about all issued rights to all objects
     * @return granted all rights
     */
    Collection<ResponseGrantedRightsApi> getGrantedRight();

    /**
     * Get information about assigned rights for a file object by its system name.
     * @param systemName System name of the file object.
     * @return Information about assigned rights.
     */
    ContainerAssignedRights getAssignedRight(final String systemName);

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
     * Get redis object - right
     * @param fileSystemObject must not be {@literal null}. Must not contain {@literal null} elements.
     * @param loginUser owner user login
     * @return redis object
     */
    List<RightUserFileSystemObjectRedis> getObj(List<String> fileSystemObject, String loginUser);

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
            fileSystemObject.forEach((obj) -> {
                String[] systemNameSplit = obj.split("\\.");
                redisList.add(buildObj(systemNameSplit[0], login, OperationRights.baseOperations()));
            });
            return redisList;
        }

        fileSystemObject.forEach((obj) -> {
            String[] systemNameSplit = obj.split("\\.");
            redisList.add(buildObj(systemNameSplit[0], login, List.of(right)));
        });

        return redisList;
    }

    /**
     * Get right objects to persist in {@literal Redis}
     * @param fileSystemObject names file system objects. Must not be {@literal null}.
     * @param login login user. Must not be {@literal null}.
     * @param right list of rights
     * @return {@literal Redis} object
     */
    default List<RightUserFileSystemObjectRedis> buildObj(@NotNull List<String> fileSystemObject, String login,
                                                          @NotNull List<OperationRights> right) {
        List<RightUserFileSystemObjectRedis> redisList = new ArrayList<>();
        fileSystemObject.forEach(obj -> redisList.add(buildObj(obj, login, right)));
        return redisList;
    }

    /**
     * Cleaning the system path from extensions and so on.
     * @param systemName System name of the file object.
     * @return The cleared system name of the file object.
     */
    default String cleaningSystemPath(String systemName) {
        return cleaningSystemPath(List.of(systemName)).get(0);
    }

    /**
     * Cleaning the system path from extensions and so on.
     * @param systemName System names of file objects.
     * @return Cleaned up system names of file objects.
     */
    default List<String> cleaningSystemPath(@NotNull Iterable<String> systemName) {
        return StreamSupport
                .stream(systemName.spliterator(), false)
                .map(name -> {
                    String[] nameSplit = name.split("\\.");
                    return nameSplit[0];
                })
                .toList();
    }

}
