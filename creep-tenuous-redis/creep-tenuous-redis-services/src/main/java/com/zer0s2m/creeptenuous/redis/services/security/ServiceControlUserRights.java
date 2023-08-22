package com.zer0s2m.creeptenuous.redis.services.security;

import java.util.Collection;
import java.util.UUID;

/**
 * Interface to implement to control user rights, such as:
 * <ul>
 *     <li>Deleting a user</li>
 * </ul>
 */
public interface ServiceControlUserRights {

    /**
     * Remove filesystem objects from redis
     * @param userLogin user login
     */
    void removeFileSystemObjects(String userLogin);

    /**
     *  Remove filesystem objects from redis by user login and system names
     * @param userLogin user login. Must not be {@literal null}.
     * @param systemNames system names of file objects. Must not contain {@literal null} elements.
     */
    void removeFileSystemObjectsBySystemNames(String userLogin, Collection<UUID> systemNames);

    /**
     * Remove granted permissions for user
     * @param userLogin user login
     */
    void removeGrantedPermissionsForUser(String userLogin);

    /**
     * Remove assigned permissions for user
     * @param userLogin user login
     */
    void removeAssignedPermissionsForUser(String userLogin);

    /**
     * Delete all tokens for a user
     * @param userLogin user login
     */
    void removeJwtTokensFotUser(String userLogin);

    /**
     * Migrate assigned rights from a remote user to another
     * @param ownerUserLogin owner user login
     * @param transferUserLogin login of the user to whom the data will be transferred
     */
    void migrateAssignedPermissionsForUser(String ownerUserLogin, String transferUserLogin);

    /**
     * Move file objects from remote user to assigned user
     * @param ownerUserLogin owner user login
     * @param transferUserLogin login of the user to whom the data will be transferred
     */
    void migrateFileSystemObjects(String ownerUserLogin, String transferUserLogin);

    /**
     * Remove rights assigned to a migrated user
     * @param ownerUserLogin owner user login
     * @param transferUserLogin login of the user to whom the data will be transferred
     */
    void deleteAssignedPermissionsForUser(String ownerUserLogin, String transferUserLogin);

    /**
     * Set system names of file objects to exclude. When distribution will be deleted.
     * <p>Set if file objects will be distributed in the future and <b>not deleted</b></p>
     * @param fileObjectsExclusions system names of file objects
     */
    void setFileObjectsExclusions(Collection<UUID> fileObjectsExclusions);

    /**
     * Set the setting for the class. Responsible whether in the future
     * the distribution of objects or they will be deleted
     * @param isDistribution is the distribution
     */
    void setIsDistribution(boolean isDistribution);

}
