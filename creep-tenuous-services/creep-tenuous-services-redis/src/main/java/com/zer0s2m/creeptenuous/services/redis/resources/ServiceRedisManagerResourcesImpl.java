package com.zer0s2m.creeptenuous.services.redis.resources;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.models.base.IBaseRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.RightUserFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.resources.ServiceRedisManagerResources;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Gathers resources to modify data in Redis.
 * <p>The {@link ServiceManagerRights} cannot live without this implementation</p>
 */
@Service("service-manager-resources-redis")
public class ServiceRedisManagerResourcesImpl implements ServiceRedisManagerResources {

    private final DirectoryRedisRepository directoryRedisRepository;

    private final FileRedisRepository fileRedisRepository;

    private final RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    @Autowired
    public ServiceRedisManagerResourcesImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository) {
        this.directoryRedisRepository = directoryRedisRepository;
        this.fileRedisRepository = fileRedisRepository;
        this.rightUserFileSystemObjectRedisRepository = rightUserFileSystemObjectRedisRepository;
    }

    /**
     * Get data about objects to move
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return result
     */
    @Override
    public List<FileRedis> getResourcesFilesForMove(List<String> ids) {
        return getResources(fileRedisRepository.findAllById(ids));
    }

    /**
     * Get data about objects to move
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return result
     */
    @Override
    public List<DirectoryRedis> getResourcesDirectoriesForMove(List<String> ids) {
        return getResources(directoryRedisRepository.findAllById(ids));
    }

    /**
     * Get data about object to delete
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @param userLogin Login of a third-party user who has rights to the object .must not be {@literal null}.
     * @return result
     */
    @Override
    public List<FileRedis> getResourcesFileForDelete(List<String> ids, String userLogin) {
        return getResources(fileRedisRepository.findAllById(ids))
                .stream()
                .filter(entity -> entity.getUserLogins() != null && entity.getUserLogins().contains(userLogin))
                .collect(Collectors.toList());
    }

    /**
     * Get data about object to delete
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @param userLogin Login of a third-party user who has rights to the object .must not be {@literal null}.
     * @return result
     */
    @Override
    public List<DirectoryRedis> getResourcesDirectoryForDelete(List<String> ids, String userLogin) {
        return getResources(directoryRedisRepository.findAllById(ids))
                .stream()
                .filter(entity -> entity.getUserLogins() != null && entity.getUserLogins().contains(userLogin))
                .collect(Collectors.toList());
    }

    /**
     * Convert iterable object to array
     * @param iterable iterable object
     * @return data array
     * @param <T> the type of elements returned by the iterator
     */
    private <T> List<T> getResources(final @NotNull Iterable<T> iterable) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(iterable.iterator(), Spliterator.ORDERED), false)
                .collect(Collectors.toList());
    }

    /**
     * Get data about object directory
     * @param id id must not be {@literal null}.
     * @return result
     */
    @Override
    public DirectoryRedis getResourceDirectoryRedis(String id) {
        Optional<DirectoryRedis> directoryRedisOptional = directoryRedisRepository.findById(id);
        return directoryRedisOptional.orElse(null);
    }

    /**
     * Get data about object directories
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return result
     */
    @Override
    public List<DirectoryRedis> getResourceDirectoryRedis(Iterable<String> ids) {
        return getResources(directoryRedisRepository.findAllById(ids));
    }

    /**
     * Get data about object file
     * @param id id must not be {@literal null}.
     * @return result
     */
    @Override
    public FileRedis getResourceFileRedis(String id) {
        Optional<FileRedis> fileRedisOptional = fileRedisRepository.findById(id);
        return fileRedisOptional.orElse(null);
    }

    /**
     * Get data about object files
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return result
     */
    @Override
    public List<FileRedis> getResourceFileRedis(Iterable<String> ids) {
        return getResources(fileRedisRepository.findAllById(ids));
    }

    /**
     * Get data about object directories by user login
     * @param userLogin user login. Must not be {@literal null}.
     * @return result
     */
    @Override
    public List<DirectoryRedis> getResourceDirectoryRedisByLoginUser(String userLogin) {
        DirectoryRedis directoryRedisExample = new DirectoryRedis();
        directoryRedisExample.setLogin(userLogin);

        return getResources(directoryRedisRepository.findAll(Example.of(directoryRedisExample)));
    }

    /**
     * Get data about object directories by user login and system names
     * @param userLogin user login. Must not be {@literal null}.
     * @param systemNames system names of file objects. Must not contain {@literal null} elements.
     * @return result
     */
    @Override
    public List<DirectoryRedis> getResourceDirectoryRedisByLoginUserAndInSystemNames(
            final String userLogin, @NotNull Collection<UUID> systemNames) {
        List<String> systemNamesStr = systemNames
                .stream()
                .map(UUID::toString)
                .toList();

        return getResourceDirectoryRedis(systemNamesStr)
                .stream()
                .filter(obj -> obj.getLogin().equals(userLogin))
                .toList();
    }

    /**
     * Get data about object files by user login
     * @param userLogin user login. Must not be {@literal null}.
     * @return result
     */
    @Override
    public List<FileRedis> getResourceFileRedisByLoginUser(String userLogin) {
        FileRedis fileRedisExample = new FileRedis();
        fileRedisExample.setLogin(userLogin);

        return getResources(fileRedisRepository.findAll(Example.of(fileRedisExample)));
    }

    /**
     * Get data about object files by user login and system names
     * @param userLogin user login. Must not be {@literal null}.
     * @param systemNames system names of file objects. Must not contain {@literal null} elements.
     * @return result
     */
    @Override
    public List<FileRedis> getResourceFileRedisByLoginUserAndInSystemNames(
            final String userLogin, @NotNull Collection<UUID> systemNames) {
        List<String> systemNamesStr = systemNames
                .stream()
                .map(UUID::toString)
                .toList();

        return getResourceFileRedis(systemNamesStr)
                .stream()
                .filter(obj -> obj.getLogin().equals(userLogin))
                .toList();
    }

    /**
     * Check if files exist by system names and username
     * @param systemNames system names of file objects
     * @param userLogin user login
     * @return if exists
     */
    @Override
    public long checkIfFilesRedisExistBySystemNamesAndUserLogin(
            @NotNull Collection<String> systemNames, String userLogin) {
        return getResourceFileRedisByLoginUserAndInSystemNames(
                userLogin,
                systemNames
                        .stream()
                        .map(UUID::fromString)
                        .toList()
        ).size();
    }

    /**
     * Check if directories exist by system names and username
     * @param systemNames system names of file objects
     * @param userLogin user login
     * @return if exists
     */
    @Override
    public long checkIfDirectoryRedisExistBySystemNamesAndUserLogin(
            @NotNull Collection<String> systemNames, String userLogin) {
        return getResourceDirectoryRedisByLoginUserAndInSystemNames(
                userLogin,
                systemNames
                        .stream()
                        .map(UUID::fromString)
                        .toList()
        ).size();
    }

    /**
     * Check if a file object is a directory type
     * @param id id must not be {@literal null}.
     * @return verified
     */
    @Override
    public boolean checkFileObjectDirectoryType(final String id) {
        return directoryRedisRepository.existsById(id);
    }

    /**
     * Get the real name of a file object.
     * @param id id must not be {@literal null}.
     * @return Real name.
     */
    @Override
    public Optional<String> getRealNameFileObject(final String id) {
        DirectoryRedis directoryRedis = getResourceDirectoryRedis(id);
        FileRedis fileRedis = getResourceFileRedis(id);

        if (directoryRedis != null && fileRedis == null) {
            return Optional.of(directoryRedis.getRealName());
        } else if (fileRedis != null && directoryRedis == null) {
            return Optional.of(fileRedis.getRealName());
        }

        return Optional.empty();
    }

    /**
     * collect real names of file objects and assign them to system names.
     *
     * @param objects File objects.
     * @return Real names related to system names.
     */
    @Override
    public HashMap<String, String> collectRealNamesFileObjectsClassifyAsSystem(
            final @NotNull Iterable<? extends IBaseRedis> objects) {
        final HashMap<String, String> realNameAsSystem = new HashMap<>();

        objects.forEach(object -> realNameAsSystem
                .put(object.getSystemName(), object.getRealName()));

        return realNameAsSystem;
    }

    /**
     * Obtain rights to interact with file objects using the user login.
     * @param userLogin User login. Must not be {@literal null}.
     * @return Rights.
     */
    @Override
    public List<RightUserFileSystemObjectRedis> getRightUserFileSystemObjectByLogin(
            final String userLogin) {
        RightUserFileSystemObjectRedis rightUserFileSystemObjectRedisExample =
                new RightUserFileSystemObjectRedis();
        rightUserFileSystemObjectRedisExample.setLogin(userLogin);

        return getResources(rightUserFileSystemObjectRedisRepository
                .findAll(Example.of(rightUserFileSystemObjectRedisExample)));
    }

}
