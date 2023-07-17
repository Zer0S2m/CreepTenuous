package com.zer0s2m.creeptenuous.redis.services.security;

/**
 * Interface to implement to control user rights, such as:
 * <ul>
 *     <li>Deleting a user</li>
 * </ul>
 */
public interface ServiceControlUserRights {

    /**
     * remove filesystem objects from redis
     * @param userLogin user login
     */
    void removeFileSystemObjects(String userLogin);

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

}
