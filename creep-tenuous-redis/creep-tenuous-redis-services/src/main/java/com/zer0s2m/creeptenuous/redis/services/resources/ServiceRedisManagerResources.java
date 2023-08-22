package com.zer0s2m.creeptenuous.redis.services.resources;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

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

    /**
     * Get data about object directories
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return result
     */
    List<DirectoryRedis> getResourceDirectoryRedis(Iterable<String> ids);

    /**
     * Get data about object file
     * @param id id must not be {@literal null}.
     * @return result
     */
    FileRedis getResourceFileRedis(String id);

    /**
     * Get data about object files
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return result
     */
    List<FileRedis> getResourceFileRedis(Iterable<String> ids);

    /**
     * Get data about object directories by user login
     * @param userLogin user login. Must not be {@literal null}.
     * @return result
     */
    List<DirectoryRedis> getResourceDirectoryRedisByLoginUser(String userLogin);

    /**
     * Get data about object directories by user login and system names
     * @param userLogin user login. Must not be {@literal null}.
     * @param systemNames system names of file objects. Must not contain {@literal null} elements.
     * @return result
     */
    List<DirectoryRedis> getResourceDirectoryRedisByLoginUserAndInSystemNames(
            String userLogin, Collection<UUID> systemNames);

    /**
     * Get data about object files by user login
     * @param userLogin user login. Must not be {@literal null}.
     * @return result
     */
    List<FileRedis> getResourceFileRedisByLoginUser(String userLogin);

    /**
     * Get data about object files by user login and system names
     * @param userLogin user login. Must not be {@literal null}.
     * @param systemNames system names of file objects. Must not contain {@literal null} elements.
     * @return result
     */
    List<FileRedis> getResourceFileRedisByLoginUserAndInSystemNames(
            String userLogin, Collection<UUID> systemNames);

    /**
     * Check if files exist by system names and username
     * @param systemNames system names of file objects
     * @param userLogin user login
     * @return if exists
     */
    long checkIfFilesRedisExistBySystemNamesAndUserLogin(Collection<String> systemNames, String userLogin);

    /**
     * Check if directories exist by system names and username
     * @param systemNames system names of file objects
     * @param userLogin user login
     * @return if exists
     */
    long checkIfDirectoryRedisExistBySystemNamesAndUserLogin(Collection<String> systemNames, String userLogin);

    /**
     * Check if a file object is a directory type
     * @param id id must not be {@literal null}.
     * @return verified
     */
    boolean checkFileObjectDirectoryType(final String id);

}
