package com.zer0s2m.creeptenuous.services.redis.security;

import com.zer0s2m.creeptenuous.common.enums.ManagerRights;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.models.base.BaseRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.JwtRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.RightUserFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.resources.ServiceRedisManagerResources;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceControlUserRights;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for controlling user rights over file system objects, such as:
 * <ul>
 *     <li>Deleting a user</li>
 * </ul>
 */
@Service("service-control-user-rights")
public class ServiceControlUserRightsImpl implements ServiceControlUserRights {

    private final DirectoryRedisRepository directoryRedisRepository;

    private final FileRedisRepository fileRedisRepository;

    private final RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    private final ServiceRedisManagerResources serviceRedisManagerResources;

    private final JwtRedisRepository jwtRedisRepository;

    private Iterable<DirectoryRedis> directoryRedisIterable;

    private Iterable<FileRedis> fileRedisIterable;

    @Autowired
    public ServiceControlUserRightsImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository,
            ServiceRedisManagerResources serviceRedisManagerResources,
            JwtRedisRepository jwtRedisRepository) {
        this.directoryRedisRepository = directoryRedisRepository;
        this.fileRedisRepository = fileRedisRepository;
        this.rightUserFileSystemObjectRedisRepository = rightUserFileSystemObjectRedisRepository;
        this.serviceRedisManagerResources = serviceRedisManagerResources;
        this.jwtRedisRepository = jwtRedisRepository;
    }

    /**
     * remove filesystem objects from redis
     * @param userLogin user login
     */
    @Override
    public void removeFileSystemObjects(String userLogin) {
        setFileSystemObjects(userLogin);

        directoryRedisRepository.deleteAll(directoryRedisIterable);
        fileRedisRepository.deleteAll(fileRedisIterable);
    }

    /**
     * Remove granted permissions for user
     * @param userLogin user login
     */
    @Override
    public void removeGrantedPermissionsForUser(String userLogin) {
        setFileSystemObjects(userLogin);

        List<String> userAllUserLogins = new ArrayList<>();
        jwtRedisRepository
                .findAll()
                .forEach(jwtRedis -> userAllUserLogins.add(jwtRedis.getLogin()));
        userAllUserLogins.remove(userLogin);

        List<String> namesFileSystemObject = new ArrayList<>();
        directoryRedisIterable.forEach(obj -> namesFileSystemObject.add(obj.getSystemNameDirectory()));
        fileRedisIterable.forEach(obj -> namesFileSystemObject.add(obj.getSystemNameFile()));

        List<String> idsRights = new ArrayList<>();
        namesFileSystemObject.forEach(name -> userAllUserLogins.forEach(login ->
                idsRights.add(name + ManagerRights.SEPARATOR_UNIQUE_KEY.get() + login)));

        rightUserFileSystemObjectRedisRepository.deleteAllById(idsRights);
    }

    /**
     * Remove assigned permissions for user
     * @param userLogin user login
     */
    @Override
    public void removeAssignedPermissionsForUser(String userLogin) {
        setFileSystemObjects(userLogin);

        RightUserFileSystemObjectRedis rightUserFileSystemObjectRedisExample = new RightUserFileSystemObjectRedis();
        rightUserFileSystemObjectRedisExample.setLogin(userLogin);

        Iterable<RightUserFileSystemObjectRedis> rightUserFileSystemObjectRedis =
                rightUserFileSystemObjectRedisRepository.findAll(Example.of(rightUserFileSystemObjectRedisExample));

        List<String> namesFileSystemObject = getNamesFileSystemObjectFromRights(rightUserFileSystemObjectRedis);

        List<DirectoryRedis> directoryRedisList = serviceRedisManagerResources.getResourceDirectoryRedis(
                namesFileSystemObject);
        List<FileRedis> fileRedisList = serviceRedisManagerResources.getResourceFileRedis(namesFileSystemObject);

        List<DirectoryRedis> directoryRedisClean = removeUserLoginFileSystemObject(directoryRedisList, userLogin);
        List<FileRedis> fileRedisClean = removeUserLoginFileSystemObject(fileRedisList, userLogin);

        directoryRedisRepository.saveAll(directoryRedisClean);
        fileRedisRepository.saveAll(fileRedisClean);
        rightUserFileSystemObjectRedisRepository.deleteAll(rightUserFileSystemObjectRedis);
    }

    /**
     * Get file object names from user over file object interaction
     * @param rights user rights over interaction with a file object
     * @return names file system object
     */
    private @NotNull List<String> getNamesFileSystemObjectFromRights(
            @NotNull Iterable<RightUserFileSystemObjectRedis> rights) {
        List<String> names = new ArrayList<>();
        rights.forEach(right ->
                names.add(right.getFileSystemObject().split(ManagerRights.SEPARATOR_UNIQUE_KEY.get())[0]));
        return names;
    }

    /**
     * Remove user login from filesystem object when granted permission
     * @param fileSystemObject file system objects
     * @param userLogin user login
     * @return cleared file objects from user login
     * @param <E> file object type
     */
    private <E extends BaseRedis> List<E> removeUserLoginFileSystemObject(
            @NotNull List<E> fileSystemObject, String userLogin) {
        return fileSystemObject
                .stream()
                .peek(obj -> {
                    List<String> userLogins = obj.getUserLogins();
                    userLogins.remove(userLogin);
                    obj.setUserLogins(userLogins);
                })
                .toList();
    }

    /**
     * Delete all tokens for a user
     * @param userLogin user login
     */
    @Override
    public void removeJwtTokensFotUser(String userLogin) {
        jwtRedisRepository.deleteById(userLogin);
    }

    /**
     * Set file system objects
     * @param userLogin user login
     */
    private void setFileSystemObjects(String userLogin) {
        if (directoryRedisIterable == null) {
            DirectoryRedis directoryRedisExample = new DirectoryRedis();
            directoryRedisExample.setLogin(userLogin);

            Iterable<DirectoryRedis> directoryRedisList = directoryRedisRepository
                    .findAll(Example.of(directoryRedisExample));

            setDirectoryRedisIterable(directoryRedisList);
        }
        if (fileRedisIterable == null) {
            FileRedis fileRedisExample = new FileRedis();
            fileRedisExample.setLogin(userLogin);

            Iterable<FileRedis> fileRedisList = fileRedisRepository
                    .findAll(Example.of(fileRedisExample));

            setFileRedisIterable(fileRedisList);
        }
    }

    /**
     * Set file system objects of type directory
     * @param directoryRedisIterable directory redis
     */
    private void setDirectoryRedisIterable(Iterable<DirectoryRedis> directoryRedisIterable) {
        this.directoryRedisIterable = directoryRedisIterable;
    }

    /**
     * Set file system objects of type file
     * @param fileRedisIterable file redis
     */
    private void setFileRedisIterable(Iterable<FileRedis> fileRedisIterable) {
        this.fileRedisIterable = fileRedisIterable;
    }

}
