package com.zer0s2m.CreepTenuous.providers.redis.services.base;

import com.zer0s2m.CreepTenuous.services.core.exceptions.NoRightsCreateDirectoryException;

import java.util.List;

public interface IBaseServiceFileSystemRedis {
    /**
     * Validate right user (directories)
     * @param parents Real names directory
     * @param systemParents System names directory
     * @param nameDirectory System name directory
     * @throws NoRightsCreateDirectoryException When the user has no execution rights
     */
    void checkRights(List<String> parents, List<String> systemParents, String nameDirectory)
            throws NoRightsCreateDirectoryException;

    /**
     * Validate right user (files)
     * @param systemNameFiles system names files
     * @throws NoRightsCreateDirectoryException When the user has no execution right
     */
    void checkRights(List<String> systemNameFiles) throws NoRightsCreateDirectoryException;

    /**
     * Validate right user (file)
     * @param systemNameFile system name files
     * @throws NoRightsCreateDirectoryException When the user has no execution right
     */
     default void checkRights(String systemNameFile) {
         checkRights(List.of(systemNameFile));
     }
}
