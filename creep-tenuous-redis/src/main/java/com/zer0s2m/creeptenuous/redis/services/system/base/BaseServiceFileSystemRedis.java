package com.zer0s2m.creeptenuous.redis.services.system.base;

import com.zer0s2m.creeptenuous.redis.exceptions.NoRightsRedisException;

import java.util.List;

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
     * Validate right user (files)
     * @param systemNameFiles system names files
     * @throws NoRightsRedisException When the user has no execution right
     */
    void checkRights(List<String> systemNameFiles) throws NoRightsRedisException;

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
     * @throws NoRightsRedisException When the user has no execution right
     */
     default void checkRights(String systemNameFile) {
         checkRights(List.of(systemNameFile));
     }
}
