package com.zer0s2m.creeptenuous.services.redis.security;

import com.zer0s2m.creeptenuous.common.enums.ManagerRights;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.models.base.BaseRedis;
import com.zer0s2m.creeptenuous.redis.models.base.IBaseRedis;
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    private Collection<UUID> fileObjectsExclusions = null;

    /**
     * the setting determines whether the distribution of file objects will be performed or they will be deleted
     */
    private boolean isDistribution = false;

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
     *  Remove filesystem objects from redis by user login and system names
     * @param userLogin user login. Must not be {@literal null}.
     * @param systemNames system names of file objects. Must not contain {@literal null} elements.
     */
    @Override
    public void removeFileSystemObjectsBySystemNames(
            String userLogin, @NotNull Collection<UUID> systemNames) {
        setFileSystemObjects(userLogin);

        List<String> systemNamesStr = systemNames
                .stream()
                .map(UUID::toString)
                .toList();

        Iterable<String> directoryRedisIterableForDelete = StreamSupport
                .stream(directoryRedisIterable
                        .spliterator(), false)
                .map(IBaseRedis::getSystemName)
                .filter(systemNamesStr::contains)
                .toList();
        Iterable<String> fileRedisIterableForDelete = StreamSupport
                .stream(fileRedisIterable
                        .spliterator(), false)
                .map(IBaseRedis::getSystemName)
                .filter(systemNamesStr::contains)
                .toList();

        directoryRedisRepository.deleteAllById(directoryRedisIterableForDelete);
        fileRedisRepository.deleteAllById(fileRedisIterableForDelete);
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

        List<String> namesFileSystemObject = getNamesFileSystemObjectFilterByFileObjectsExclusions(
                isDistribution ? fileObjectsExclusions : null
        );

        List<String> idsRights = new ArrayList<>();
        namesFileSystemObject.forEach(name -> userAllUserLogins.forEach(login ->
                idsRights.add(name + ManagerRights.SEPARATOR_UNIQUE_KEY.get() + login)));

        rightUserFileSystemObjectRedisRepository.deleteAllById(idsRights);
    }

    /**
     * Get all system names of file objects and filter by names that are in the exclusion list
     * {@link ServiceControlUserRights#setFileObjectsExclusions(Collection)}
     * @param systemNames system names of file objects
     * @return filtered list of system names
     */
    private @NotNull List<String> getNamesFileSystemObjectFilterByFileObjectsExclusions(Collection<UUID> systemNames) {
        List<String> namesFileSystemObject = new ArrayList<>();

        directoryRedisIterable.forEach(obj -> namesFileSystemObject.add(obj.getSystemName()));
        fileRedisIterable.forEach(obj -> namesFileSystemObject.add(obj.getSystemName()));

        if (systemNames != null) {
            return namesFileSystemObject
                    .stream()
                    .filter(systemName -> systemNames.contains(UUID.fromString(systemName)))
                    .toList();
        }

        return namesFileSystemObject;
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
        if (isDistribution && fileObjectsExclusions != null) {
            namesFileSystemObject = namesFileSystemObject
                    .stream()
                    .filter(systemName -> fileObjectsExclusions.contains(UUID.fromString(systemName)))
                    .toList();
        }

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
                    if (userLogins != null) {
                        userLogins.remove(userLogin);
                        obj.setUserLogins(userLogins);
                    }
                })
                .toList();
    }

    /**
     * Remove user login from filesystem object when granted permission
     * @param fileSystemObject file system objects
     * @param userLogin user login
     * @return cleared file objects from user login
     * @param <E> file object type
     */
    private <E extends BaseRedis> List<E> removeUserLoginFileSystemObject(
            @NotNull Iterable<E> fileSystemObject, String userLogin) {
        return removeUserLoginFileSystemObject(
                StreamSupport
                        .stream(Spliterators.spliteratorUnknownSize(
                                fileSystemObject.iterator(), Spliterator.ORDERED), false)
                        .collect(Collectors.toList()),
                userLogin
        );
    }

    /**
     * Migrate new user login from filesystem object when granting permission
     * @param fileSystemObject file system objects
     * @param newUserLogin new login of the user to whom the data is transferred
     * @param oldUserLogin previous owner's login
     * @return clean set of objects with new assigned user
     * @param <E> file object type
     */
    private <E extends BaseRedis> List<E> migrateUserLoginFileSystemObject(
            @NotNull List<E> fileSystemObject, String oldUserLogin, String newUserLogin) {
        return fileSystemObject
                .stream()
                .peek(obj -> {
                    List<String> userLogins = obj.getUserLogins();
                    if (userLogins != null) {
                        userLogins.remove(oldUserLogin);
                        userLogins.add(newUserLogin);
                        obj.setUserLogins(userLogins);
                    } else {
                        obj.setUserLogins(List.of(newUserLogin));
                    }
                })
                .toList();
    }

    /**
     * Move file objects from remote user to assigned user
     * @param fileSystemObject file system objects
     * @param newUserLogin new login of the user to whom the data is transferred
     * @return clean set of objects with new assigned user
     * @param <E> file object type
     */
    private <E extends BaseRedis> List<E> migrateFileObjectsSystem(
            @NotNull List<E> fileSystemObject, String newUserLogin) {
        return fileSystemObject
                .stream()
                .peek(obj -> obj.setLogin(newUserLogin))
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
     *
     * @param userLogin user login
     */
    private void setFileSystemObjects(String userLogin) {
        DirectoryRedis directoryRedisExample = new DirectoryRedis();
        directoryRedisExample.setLogin(userLogin);

        Iterable<DirectoryRedis> directoryRedisList = directoryRedisRepository
                .findAll(Example.of(directoryRedisExample));

        setDirectoryRedisIterable(directoryRedisList);

        FileRedis fileRedisExample = new FileRedis();
        fileRedisExample.setLogin(userLogin);

        Iterable<FileRedis> fileRedisList = fileRedisRepository
                .findAll(Example.of(fileRedisExample));

        setFileRedisIterable(fileRedisList);
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

    /**
     * Transferring assigned rights from a remote user to another.
     * <p>If the owner had rights to other file objects (except the user to which they
     * will be transferred), then they should be transferred to the new</p>
     * @param ownerUserLogin owner user login
     * @param transferUserLogin login of the user to whom the data will be transferred
     */
    @Override
    public void migrateAssignedPermissionsForUser(String ownerUserLogin, String transferUserLogin) {
        RightUserFileSystemObjectRedis rightUserFileSystemObjectRedisExample = new RightUserFileSystemObjectRedis();
        rightUserFileSystemObjectRedisExample.setLogin(ownerUserLogin);

        Iterable<RightUserFileSystemObjectRedis> rightUserFileSystemObjectRedis =
                rightUserFileSystemObjectRedisRepository.findAll(Example.of(rightUserFileSystemObjectRedisExample));

        List<RightUserFileSystemObjectRedis> rightUserFileSystemObjectRedisClean =
                StreamSupport
                        .stream(Spliterators.spliteratorUnknownSize(
                                rightUserFileSystemObjectRedis.iterator(), Spliterator.ORDERED), false)
                        .peek(obj -> {
                            obj.setLogin(transferUserLogin);
                            obj.setFileSystemObject(
                                    obj.getFileSystemObject().split(ManagerRights.SEPARATOR_UNIQUE_KEY.get())[0] +
                                            ManagerRights.SEPARATOR_UNIQUE_KEY.get() + transferUserLogin
                            );
                        })
                        .toList();

        List<String> namesFileSystemObject = getNamesFileSystemObjectFromRights(rightUserFileSystemObjectRedis);

        List<DirectoryRedis> directoryRedisList = serviceRedisManagerResources.getResourceDirectoryRedis(
                namesFileSystemObject);
        List<FileRedis> fileRedisList = serviceRedisManagerResources.getResourceFileRedis(namesFileSystemObject);

        List<DirectoryRedis> directoryRedisRaw = removeUserLoginFileSystemObject(directoryRedisList, ownerUserLogin);
        List<FileRedis> fileRedisRaw = removeUserLoginFileSystemObject(fileRedisList, ownerUserLogin);
        List<DirectoryRedis> directoryRedisClean = migrateUserLoginFileSystemObject(
                directoryRedisRaw, ownerUserLogin, transferUserLogin);
        List<FileRedis> fileRedisClean = migrateUserLoginFileSystemObject(
                fileRedisRaw, ownerUserLogin, transferUserLogin);

        directoryRedisRepository.saveAll(directoryRedisClean);
        fileRedisRepository.saveAll(fileRedisClean);
        rightUserFileSystemObjectRedisRepository.saveAll(rightUserFileSystemObjectRedisClean);

        deleteAssignedPermissionsForUserAfterMigrate(transferUserLogin);
    }

    /**
     * Delete the user entry for file object permissions if that user is the owner.
     * <p>Apply after method {@link ServiceControlUserRights#migrateAssignedPermissionsForUser(String, String)}</p>
     * @param userLogin user login
     */
    private void deleteAssignedPermissionsForUserAfterMigrate(String userLogin) {
        setFileSystemObjects(userLogin);

        List<DirectoryRedis> directoryRedisClean = removeUserLoginFileSystemObject(
                directoryRedisIterable, userLogin);
        List<FileRedis> fileRedisClean = removeUserLoginFileSystemObject(fileRedisIterable, userLogin);

        directoryRedisRepository.saveAll(directoryRedisClean);
        fileRedisRepository.saveAll(fileRedisClean);
    }

    /**
     * Move file objects from remote user to assigned user
     * @param ownerUserLogin owner user login
     * @param transferUserLogin login of the user to whom the data will be transferred
     */
    @Override
    public void migrateFileSystemObjects(String ownerUserLogin, String transferUserLogin) {
        setFileSystemObjects(ownerUserLogin);

        List<DirectoryRedis> directoryRedisList = StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(
                        directoryRedisIterable.iterator(), Spliterator.ORDERED), false)
                .toList();
        List<FileRedis> fileRedisList = StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(
                        fileRedisIterable.iterator(), Spliterator.ORDERED), false)
                .toList();

        List<DirectoryRedis> directoryRedisRaw = removeUserLoginFileSystemObject(directoryRedisList, transferUserLogin);
        List<FileRedis> fileRedisRaw = removeUserLoginFileSystemObject(fileRedisList, transferUserLogin);
        List<DirectoryRedis> directoryRedisClean = migrateFileObjectsSystem(directoryRedisRaw, transferUserLogin);
        List<FileRedis> fileRedisClean = migrateFileObjectsSystem(fileRedisRaw, transferUserLogin);

        directoryRedisRepository.saveAll(directoryRedisClean);
        fileRedisRepository.saveAll(fileRedisClean);
    }

    /**
     * Remove the rights assigned to the migrated user. Necessary so that an already assigned
     * user does not have rights to their own new file objects
     * @param ownerUserLogin owner user login
     * @param transferUserLogin login of the user to whom the data will be transferred
     */
    @Override
    public void deleteAssignedPermissionsForUser(String ownerUserLogin, String transferUserLogin) {
        setFileSystemObjects(ownerUserLogin);

        RightUserFileSystemObjectRedis rightUserFileSystemObjectRedisExample = new RightUserFileSystemObjectRedis();
        rightUserFileSystemObjectRedisExample.setLogin(transferUserLogin);

        List<RightUserFileSystemObjectRedis> rightUserFileSystemObjectRedis =
                StreamSupport
                        .stream(Spliterators.spliteratorUnknownSize(
                                rightUserFileSystemObjectRedisRepository
                                        .findAll(Example.of(rightUserFileSystemObjectRedisExample))
                                        .iterator(),
                                Spliterator.ORDERED), false)
                        .toList();

        List<String> namesFileSystemObjects = new ArrayList<>();
        fileRedisIterable.forEach(fileRedis -> namesFileSystemObjects.add(fileRedis.getSystemName()));
        directoryRedisIterable.forEach(directoryRedis -> namesFileSystemObjects.add(directoryRedis.getSystemName()));

        rightUserFileSystemObjectRedisRepository.deleteAll(
                rightUserFileSystemObjectRedis
                        .stream()
                        .filter(obj -> namesFileSystemObjects
                                .contains(obj
                                        .getFileSystemObject()
                                        .split(ManagerRights.SEPARATOR_UNIQUE_KEY.get())[0]))
                        .toList());
    }

    /**
     * Set system names of file objects to exclude. When distribution will be deleted.
     * <p>Set if file objects will be distributed in the future and <b>not deleted</b></p>
     * @param fileObjectsExclusions system names of file objects
     */
    @Override
    public void setFileObjectsExclusions(Collection<UUID> fileObjectsExclusions) {
        this.fileObjectsExclusions = fileObjectsExclusions;
    }

    /**
     * Set the setting for the class. Responsible whether in the future
     * the distribution of objects or they will be deleted
     * @param isDistribution is the distribution
     */
    @Override
    public void setIsDistribution(boolean isDistribution) {
        this.isDistribution = isDistribution;
    }

}
