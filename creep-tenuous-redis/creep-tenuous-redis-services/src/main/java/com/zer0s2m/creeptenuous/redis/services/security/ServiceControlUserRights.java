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

}
