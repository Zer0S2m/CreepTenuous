package com.zer0s2m.creeptenuous.services.redis.resources;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.resources.ServiceRedisManagerResources;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
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

    @Autowired
    public ServiceRedisManagerResourcesImpl(
            DirectoryRedisRepository directoryRedisRepository, FileRedisRepository fileRedisRepository) {
        this.directoryRedisRepository = directoryRedisRepository;
        this.fileRedisRepository = fileRedisRepository;
    }

    /**
     * Get data about objects to move
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return result
     */
    @Override
    public List<FileRedis> getResourcesFilesForMove(List<String> ids) {
        return getResourcesForOperation(fileRedisRepository.findAllById(ids));
    }

    /**
     * Get data about objects to move
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return result
     */
    @Override
    public List<DirectoryRedis> getResourcesDirectoriesForMove(List<String> ids) {
        return getResourcesForOperation(directoryRedisRepository.findAllById(ids));
    }

    /**
     * Get data about object to delete
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @param userLogin Login of a third-party user who has rights to the object .must not be {@literal null}.
     * @return result
     */
    @Override
    public List<FileRedis> getResourcesFileForDelete(List<String> ids, String userLogin) {
        return getResourcesForOperation(fileRedisRepository.findAllById(ids))
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
        return getResourcesForOperation(directoryRedisRepository.findAllById(ids))
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
    private <T> List<T> getResourcesForOperation(final @NotNull Iterable<T> iterable) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(iterable.iterator(), Spliterator.ORDERED), false)
                .collect(Collectors.toList());
    }

}
