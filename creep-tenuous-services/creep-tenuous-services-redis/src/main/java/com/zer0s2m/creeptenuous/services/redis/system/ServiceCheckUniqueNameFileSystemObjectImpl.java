package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.common.exceptions.ExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.resources.ServiceRedisManagerResources;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCheckUniqueNameFileSystemObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for checking for uniqueness of the name of the created file object at different directory levels
 */
@Service("service-check-unique-name-file-system-object")
public class ServiceCheckUniqueNameFileSystemObjectImpl implements ServiceCheckUniqueNameFileSystemObject {

    private final ServiceRedisManagerResources serviceRedisManagerResources;

    @Autowired
    public ServiceCheckUniqueNameFileSystemObjectImpl(ServiceRedisManagerResources serviceRedisManagerResources) {
        this.serviceRedisManagerResources = serviceRedisManagerResources;
    }

    /**
     * Check name for uniqueness depending on directory level
     * @param realName the new name of the object being created
     * @param ids file object names at any directory level. Must not be {@literal null} nor contain any
     * {@literal null} values.
     * @throws ExistsFileSystemObjectRedisException uniqueness of the name in the system under
     * different directory levels
     */
    @Override
    public void checkUniqueName(String realName, Iterable<String> ids)
            throws ExistsFileSystemObjectRedisException {
        List<DirectoryRedis> directoryRedisList = serviceRedisManagerResources.getResourceDirectoryRedis(ids);
        List<FileRedis> fileRedisList = serviceRedisManagerResources.getResourceFileRedis(ids);

        List<String> realNamesFileSystemObject = new ArrayList<>();

        directoryRedisList.forEach(directoryRedis ->
                realNamesFileSystemObject.add(directoryRedis.getRealNameDirectory()));
        fileRedisList.forEach(directoryRedis ->
                realNamesFileSystemObject.add(directoryRedis.getRealNameFile()));

        if (realNamesFileSystemObject.contains(realName)) {
            throw new ExistsFileSystemObjectRedisException();
        }
    }

}
