package com.zer0s2m.CreepTenuous.providers.redis.services.base;

import com.zer0s2m.CreepTenuous.services.directory.create.exceptions.NoRightsCreateDirectoryException;

import java.util.List;

public interface IBaseServiceFileSystemRedis {
    void checkRights(List<String> parents, List<String> systemParents, String nameDirectory)
            throws NoRightsCreateDirectoryException;
}
