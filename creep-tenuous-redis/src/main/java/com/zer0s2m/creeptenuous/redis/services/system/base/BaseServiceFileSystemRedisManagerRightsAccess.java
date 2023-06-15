package com.zer0s2m.creeptenuous.redis.services.system.base;

import com.zer0s2m.creeptenuous.redis.exceptions.NoRightsRedisException;
import com.zer0s2m.creeptenuous.redis.services.security.BaseServiceManagerRightsAccess;

import java.util.List;

/**
 * Basic service for serving file system objects in Redis.
 * <ul>
 *     <li>Checking Ownership of an Object</li>
 * </ul>
 */
public interface BaseServiceFileSystemRedisManagerRightsAccess extends BaseServiceManagerRightsAccess {

    /**
     * Validate right user (directories)
     * @param parents Real names directory
     * @param systemParents System names directory
     * @param nameDirectory System name directory
     * @return are there existing rights
     * @throws NoRightsRedisException When the user has no execution rights
     */
    boolean checkRights(List<String> parents, List<String> systemParents, String nameDirectory)
            throws NoRightsRedisException;

    /**
     * Validate right user (directories)
     * @param parents Real names directory
     * @param systemParents System names directory
     * @param nameDirectory System name directory
     * @param isException whether to raise an exception if there are no rights
     * @return are there existing rights
     */
    boolean checkRights(List<String> parents, List<String> systemParents, String nameDirectory, boolean isException);

    /**
     * Validate right user (files and directories)
     * @param systemName system name file system object
     * @return is rights
     * @throws NoRightsRedisException When the user has no execution right
     */
    boolean checkRights(List<String> systemName) throws NoRightsRedisException;

    /**
     * Enable check right inclusive name directory
     * @param enableCheckIsNameDirectory is enabled
     */
    void setEnableCheckIsNameDirectory(Boolean enableCheckIsNameDirectory);

    /**
     * Reset
     * @param resetCheckIsNameDirectory is enabled
     */
    void setResetCheckIsNameDirectory(Boolean resetCheckIsNameDirectory);

    /**
     * Validate right user (file)
     * @param systemNameFile system name files
     * @return is rights
     * @throws NoRightsRedisException When the user has no execution right
     */
     default boolean checkRights(String systemNameFile) {
         return checkRights(List.of(systemNameFile));
     }

    /**
     * Setting whether to raise an exception
     * @param isException whether to raise an exception if there are no rights
     */
    void setIsException(boolean isException);

    /**
     * Getting whether to raise an exception
     * @return whether to raise an exception if there are no rights
     */
    boolean getIsException();

}
