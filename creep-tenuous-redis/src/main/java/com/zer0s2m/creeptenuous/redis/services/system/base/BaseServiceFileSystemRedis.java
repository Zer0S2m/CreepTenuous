package com.zer0s2m.creeptenuous.redis.services.system.base;

import com.zer0s2m.creeptenuous.redis.exceptions.NoRightsRedisException;

import java.util.List;

/**
 * Basic service for serving file system objects in Redis.
 * <ul>
 *     <li>Checking Ownership of an Object</li>
 * </ul>
 */
public interface BaseServiceFileSystemRedis {

    /**
     * Validate right user (directories)
     * @param parents Real names directory
     * @param systemParents System names directory
     * @param nameDirectory System name directory
     * @throws NoRightsRedisException When the user has no execution rights
     */
    void checkRights(List<String> parents, List<String> systemParents, String nameDirectory)
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
     * Validate right user (files)
     * @param systemNameFiles system names files
     * @return is rights
     * @throws NoRightsRedisException When the user has no execution right
     */
    boolean checkRights(List<String> systemNameFiles) throws NoRightsRedisException;

    /**
     * Set access token
     * @param accessToken <b>JWT</b> access token
     */
    void setAccessToken(String accessToken);

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
