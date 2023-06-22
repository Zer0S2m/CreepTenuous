package com.zer0s2m.creeptenuous.redis.services.resources;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;

import java.util.List;

/**
 * Gathers resources to modify data in Redis.
 * <p>The {@link ServiceManagerRights} cannot live without this implementation</p>
 */
public interface ServiceRedisManagerResources {

    /**
     * Get data about objects to move
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return result
     */
    List<FileRedis> getResourcesFilesForMove(List<String> ids);

    /**
     * Get data about objects to move
     * @param id must not be {@literal null}.
     * @return result
     */
    default List<FileRedis> getResourcesFilesForMove(String id) {
        return getResourcesFilesForMove(List.of(id));
    }

    /**
     * Get data about objects to move
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return result
     */
    List<DirectoryRedis> getResourcesDirectoriesForMove(List<String> ids);

    /**
     * Get data about objects to move
     * @param id must not be {@literal null}.
     * @return result
     */
    default List<DirectoryRedis> getResourcesDirectoriesForMove(String id) {
        return getResourcesDirectoriesForMove(List.of(id));
    }

    /**
     * Get data about object to delete
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @param userLogin Login of a third-party user who has rights to the object .must not be {@literal null}.
     * @return result
     */
    List<FileRedis> getResourcesFileForDelete(List<String> ids, String userLogin);

    /**
     * Get data about object to delete
     * @param id must not be {@literal null}.
     * @param userLogin Login of a third-party user who has rights to the object .must not be {@literal null}.
     * @return result
     */
    default List<FileRedis> getResourcesFileForDelete(String id, String userLogin) {
        return getResourcesFileForDelete(List.of(id), userLogin);
    }

    /**
     * Get data about object to delete
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @param userLogin Login of a third-party user who has rights to the object .must not be {@literal null}.
     * @return result
     */
    List<DirectoryRedis> getResourcesDirectoryForDelete(List<String> ids, String userLogin);

    /**
     * Get data about object to delete
     * @param id must not be {@literal null}.
     * @return result
     */
    default List<DirectoryRedis> getResourcesDirectoryForDelete(String id, String userLogin) {
        return getResourcesDirectoryForDelete(List.of(id), userLogin);
    }

    /**
     * Get data about object directory
     * @param id id must not be {@literal null}.
     * @return result
     */
    DirectoryRedis getResourceDirectoryRedis(String id);

}
