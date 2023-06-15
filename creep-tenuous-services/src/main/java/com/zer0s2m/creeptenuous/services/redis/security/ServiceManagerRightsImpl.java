package com.zer0s2m.creeptenuous.services.redis.security;

import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.redis.exceptions.ChangeRightsYourselfException;
import com.zer0s2m.creeptenuous.redis.exceptions.NoExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.redis.exceptions.NoExistsRightException;
import com.zer0s2m.creeptenuous.redis.exceptions.NoRightsRedisException;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.models.base.BaseRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.JwtRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.RightUserFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.redis.services.resources.ServiceRedisManagerResources;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.utils.JwtUtils;
import com.zer0s2m.creeptenuous.services.system.utils.WalkDirectoryInfo;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing user rights for interacting with a target file system object
 */
@Service("service-manager-rights")
public class ServiceManagerRightsImpl implements ServiceManagerRights {

    protected final JwtProvider jwtProvider;

    protected Claims accessClaims;

    private String loginUser;

    private boolean isWillBeCreated = true;

    private boolean isDirectory = true;

    protected final DirectoryRedisRepository directoryRedisRepository;

    protected final FileRedisRepository fileRedisRepository;

    protected final JwtRedisRepository jwtRedisRepository;

    private final RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    private final ServiceRedisManagerResources serviceRedisManagerResources;

    @Autowired
    public ServiceManagerRightsImpl(
            JwtProvider jwtProvider,
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository,
            JwtRedisRepository jwtRedisRepository,
            ServiceRedisManagerResources serviceRedisManagerResources
    ) {
        this.jwtProvider = jwtProvider;
        this.directoryRedisRepository = directoryRedisRepository;
        this.fileRedisRepository = fileRedisRepository;
        this.rightUserFileSystemObjectRedisRepository = rightUserFileSystemObjectRedisRepository;
        this.jwtRedisRepository = jwtRedisRepository;
        this.serviceRedisManagerResources = serviceRedisManagerResources;
    }

    /**
     * Checking permissions to perform some action on a specific file object
     * @param operation type of transaction
     * @param fileSystemObjects file system objects
     * @throws NoRightsRedisException Insufficient rights to perform the operation
     */
    @Override
    public void checkRightsByOperation(OperationRights operation, List<String> fileSystemObjects)
            throws NoRightsRedisException {

        List<FileRedis> fileRedis = (List<FileRedis>) getFileRedis(fileSystemObjects);
        List<DirectoryRedis> directoryRedis = (List<DirectoryRedis>) getDirectoryRedis(fileSystemObjects);

        List<FileRedis> fileRedisSorted = conductorOperationFileRedis(fileRedis, operation);
        List<DirectoryRedis> directoryRedisSorted = conductorOperationDirectoryRedis(directoryRedis, operation);

        if (!((fileRedisSorted.size() + directoryRedisSorted.size()) == fileSystemObjects.size())) {
            throw new NoRightsRedisException();
        }
    }

    /**
     * Checking permissions to perform some actions on a certain file object - operation {@link OperationRights#DELETE}
     * @param fileSystemObject file system object
     * @throws IOException signals that an I/O exception of some sort has occurred
     * @throws NoRightsRedisException Insufficient rights to perform the operation
     */
    @Override
    public void checkRightByOperationDeleteDirectory(String fileSystemObject)
            throws IOException, NoRightsRedisException {
        checkRightByOperationDirectory(fileSystemObject, OperationRights.DELETE);
    }

    /**
     * Checking permissions to perform some actions on a certain file object - operation {@link OperationRights#MOVE}
     * @param fileSystemObject file system object
     * @throws IOException signals that an I/O exception of some sort has occurred
     * @throws NoRightsRedisException Insufficient rights to perform the operation
     */
    @Override
    public void checkRightByOperationMoveDirectory(String fileSystemObject)
            throws IOException, NoRightsRedisException {
        checkRightByOperationDirectory(fileSystemObject, OperationRights.MOVE);
    }

    /**
     * Checking permissions to perform some actions on a certain file object - operation {@link OperationRights#COPY}
     * @param fileSystemObject file system object
     * @throws IOException signals that an I/O exception of some sort has occurred
     * @throws NoRightsRedisException Insufficient rights to perform the operation
     */
    @Override
    public void checkRightByOperationCopyDirectory(String fileSystemObject)
            throws IOException, NoRightsRedisException {
        checkRightByOperationDirectory(fileSystemObject, OperationRights.COPY);
    }

    /**
     * Checking permissions to perform some actions on a certain file object - operation
     * {@link OperationRights#DOWNLOAD}
     * @param fileSystemObject file system object
     * @throws IOException signals that an I/O exception of some sort has occurred
     * @throws NoRightsRedisException Insufficient rights to perform the operation
     */
    @Override
    public void checkRightByOperationDownloadDirectory(String fileSystemObject)
            throws IOException, NoRightsRedisException {
        checkRightByOperationDirectory(fileSystemObject, OperationRights.DOWNLOAD);
    }

    /**
     * Checking permissions to perform some actions on a certain file object
     * @param fileSystemObject file system object
     * @param operation type of transaction
     * @throws IOException signals that an I/O exception of some sort has occurred
     * @throws NoRightsRedisException Insufficient rights to perform the operation
     */
    private void checkRightByOperationDirectory(String fileSystemObject, OperationRights operation)
            throws IOException, NoRightsRedisException {
        List<DirectoryRedis> directoryRedisCurrent = Streamable.of(getDirectoryRedis(List.of(fileSystemObject)))
                .stream()
                .toList();

        if (getIsDirectory() && directoryRedisCurrent.size() != 0 && directoryRedisCurrent.get(0).getIsDirectory()) {
            List<ContainerInfoFileSystemObject> attached = WalkDirectoryInfo.walkDirectory(
                    Path.of(directoryRedisCurrent.get(0).getPathDirectory().replace("76785e20-66aa-4bf7-9716-7c2785e78cb1/", "")));
            List<String> namesFileSystemObject = attached
                    .stream()
                    .map(ContainerInfoFileSystemObject::nameFileSystemObject)
                    .toList();
            List<DirectoryRedis> directoryRedisResource = serviceRedisManagerResources.getResourcesDirectoryForDelete(
                    namesFileSystemObject, getLoginUser());
            List<FileRedis> fileRedisResource = serviceRedisManagerResources.getResourcesFileForDelete(
                    namesFileSystemObject, getLoginUser());

            if (namesFileSystemObject.size() != (directoryRedisResource.size() + fileRedisResource.size())) {
                throw new NoRightsRedisException();
            } else {
                List<DirectoryRedis> directoryRedisSorted = conductorOperationDirectoryRedis(directoryRedisResource,
                        operation);
                List<FileRedis> fileRedisSorted = conductorOperationFileRedis(fileRedisResource, operation);

                if (!((fileRedisSorted.size() + directoryRedisSorted.size()) == namesFileSystemObject.size())) {
                    throw new NoRightsRedisException();
                }
            }
        }
    }

    /**
     * Create a user right on a file system object
     * @param right data right. Must not be {@literal null}.
     * @throws ChangeRightsYourselfException Change rights over the interaction of file system objects to itself
     */
    @Override
    public void addRight(RightUserFileSystemObjectRedis right) throws ChangeRightsYourselfException {
        checkAddingRightsYourself(right);

        String systemName = right.getFileSystemObject();
        Optional<RightUserFileSystemObjectRedis> existsRight = rightUserFileSystemObjectRedisRepository
                .findById(buildUniqueKey(systemName, right.getLogin()));
        if (existsRight.isPresent()) {
            RightUserFileSystemObjectRedis existsRightReady = existsRight.get();
            List<OperationRights> operationRightsList = existsRightReady.getRight();
            if (operationRightsList == null) {
                operationRightsList = new ArrayList<>();
            }
            operationRightsList.addAll(right.getRight());

            existsRightReady.setRight(operationRightsList
                    .stream()
                    .distinct()
                    .collect(Collectors.toList())
            );

            rightUserFileSystemObjectRedisRepository.save(existsRightReady);
        } else {
            right.setFileSystemObject(buildUniqueKey(right.getFileSystemObject(), right.getLogin()));
            rightUserFileSystemObjectRedisRepository.save(right);
        }

        addUserLoginsToRedisObj(systemName, right.getLogin());
    }

    /**
     * Binding a user to a file system object in Redis
     * @param systemName filesystem object system name. Must not be {@literal null}.
     * @param loginUser login user
     */
    private void addUserLoginsToRedisObj(@NotNull String systemName, String loginUser) {
        Optional<FileRedis> fileRedisOptional = fileRedisRepository.findById(systemName);
        Optional<DirectoryRedis> directoryRedisOptional = directoryRedisRepository.findById(systemName);

        fileRedisOptional.ifPresent(fileRedis -> {
            setDataUserLoginsToRedisObj(fileRedis, loginUser);
            fileRedisRepository.save(fileRedis);
        });
        directoryRedisOptional.ifPresent(directoryRedis -> {
            setDataUserLoginsToRedisObj(directoryRedis, loginUser);
            directoryRedisRepository.save(directoryRedis);
        });
    }

    /**
     * Set data for redis object
     * @param obj redis object
     * @param loginUserOther login user
     */
    private void setDataUserLoginsToRedisObj(@NotNull BaseRedis obj, String loginUserOther) {
        List<String> userLogins = obj.getUserLogins();
        if (userLogins == null) {
            obj.setUserLogins(List.of(loginUserOther));
        } else {
            if (!userLogins.contains(loginUserOther)) {
                userLogins.add(loginUserOther);
                obj.setUserLogins(userLogins);
            }
        }
    }

    /**
     * Delete a user right a file system object
     * @param right data right. Must not be {@literal null}.
     * @param operationRights type of transaction. Must not be {@literal null}.
     * @throws ChangeRightsYourselfException Change rights over the interaction of file system objects to itself
     * @throws NoExistsRightException The right was not found in the database.
     *                                Or is {@literal null} {@link NullPointerException}
     */
    public void deleteRight(RightUserFileSystemObjectRedis right, OperationRights operationRights)
            throws ChangeRightsYourselfException, NoExistsRightException {
        checkDeletingRightsYourself(right);

        List<OperationRights> operationRightsList = right.getRight();
        if (operationRightsList != null && operationRightsList.remove(operationRights)) {
            if (operationRightsList.size() == 0) {
                deleteUserLoginsToRedisObj(unpackingUniqueKey(right.getFileSystemObject()), right.getLogin());
                rightUserFileSystemObjectRedisRepository.delete(right);
            } else {
                right.setRight(operationRightsList);
                rightUserFileSystemObjectRedisRepository.save(right);
            }
        }
    }

    /**
     * Removing a User Binding to an Object in Redis
     * @param systemName filesystem object system name. Must not be {@literal null}.
     * @param loginUser login user
     */
    private void deleteUserLoginsToRedisObj(@NotNull String systemName, @NotNull String loginUser) {
        Optional<FileRedis> fileRedisOptional = fileRedisRepository.findById(systemName);
        Optional<DirectoryRedis> directoryRedisOptional = directoryRedisRepository.findById(systemName);

        if (fileRedisOptional.isPresent()) {
            FileRedis fileRedis = fileRedisOptional.get();
            if (delDataUserLoginsToRedisObj(fileRedis, loginUser)) {
                fileRedisRepository.save(fileRedis);
            }
        }
        if (directoryRedisOptional.isPresent()) {
            DirectoryRedis directoryRedis = directoryRedisOptional.get();
            if (delDataUserLoginsToRedisObj(directoryRedis, loginUser)) {
                directoryRedisRepository.save(directoryRedis);
            }
        }
    }

    /**
     * Del data for redis object
     * @param obj redis object
     * @param loginUser login user
     * @return whether removed
     */
    private boolean delDataUserLoginsToRedisObj(@NotNull BaseRedis obj, @NotNull String loginUser) {
        List<String> userLogins = obj.getUserLogins();
        if (userLogins != null && userLogins.contains(loginUser) && userLogins.remove(loginUser)) {
            obj.setUserLogins(userLogins);
            return true;
        }
        return false;
    }

    /**
     * Checking for the existence of a file system object in the database
     * @param nameFileSystemObject The system name of the file system object
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     */
    @Override
    public void isExistsFileSystemObject(String nameFileSystemObject) throws NoExistsFileSystemObjectRedisException {
        boolean isExistsDirectory = directoryRedisRepository.existsById(nameFileSystemObject);
        boolean isExistsFile = fileRedisRepository.existsById(nameFileSystemObject);

        if (!(isExistsDirectory || isExistsFile)) {
            throw new NoExistsFileSystemObjectRedisException();
        }
    }

    /**
     * Checking for the existence of a user
     * @param loginUser login user
     * @throws UserNotFoundException the user does not exist in the system
     */
    @Override
    public void isExistsUser(String loginUser) throws UserNotFoundException {
        boolean isExists = jwtRedisRepository.existsById(loginUser);
        if (!isExists) {
            throw new UserNotFoundException();
        }
    }

    /**
     * Checking for adding rights to itself
     * @param right must not be null.
     * @throws ChangeRightsYourselfException change rights over the interaction of file system objects to itself
     */
    @Override
    public void checkAddingRightsYourself(RightUserFileSystemObjectRedis right) throws ChangeRightsYourselfException {
        checkChangeRightYourself(right);
    }

    /**
     * Checking for deleting rights to itself
     * @param right must not be null.
     * @throws ChangeRightsYourselfException change rights over the interaction of file system objects to itself
     */
    @Override
    public void checkDeletingRightsYourself(RightUserFileSystemObjectRedis right)
            throws ChangeRightsYourselfException, NoExistsRightException {
        if (right == null) {
            throw new NoExistsRightException();
        }
        checkChangeRightYourself(right);
    }

    /**
     * Checking for change rights to itself
     * @param right must not be null.
     * @throws ChangeRightsYourselfException change rights over the interaction of file system objects to itself
     */
    private void checkChangeRightYourself(@NotNull RightUserFileSystemObjectRedis right)
            throws ChangeRightsYourselfException {
        if (right.getLogin().equals(getLoginUser()) && !getIsWillBeCreated()) {
            throw new ChangeRightsYourselfException();
        }
    }

    /**
     * Set access token
     * @param accessToken <b>JWT</b> access token
     */
    @Override
    public void setAccessToken(String accessToken) {
        setAccessClaims(JwtUtils.getPureAccessToken(accessToken));
    }

    /**
     * Getting login user
     * @return login user
     */
    @Override
    public String getLoginUser() {
        return this.loginUser;
    }

    /**
     * Setting login user
     */
    @Override
    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    /**
     * Set setting. Responsible for regulating validation prior to creating or deleting an object.
     * Necessary to avoid exceptions - {@link ChangeRightsYourselfException}.
     * @param isWillBeCreated whether the object will be created
     */
    @Override
    public void setIsWillBeCreated(boolean isWillBeCreated) {
        this.isWillBeCreated = isWillBeCreated;
    }

    /**
     * Get setting. Responsible for regulating validation prior to creating or deleting an object.
     * @return whether the object will be created
     */
    @Override
    public boolean getIsWillBeCreated() {
        return isWillBeCreated;
    }

    /**
     * Set the parameter responsible for the type of file system object, file or directory
     * @param isDirectory is directory
     */
    @Override
    public void setIsDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    /**
     * Get the parameter responsible for the file system object type, file or directory
     * @return is directory
     */
    @Override
    public boolean getIsDirectory() {
        return isDirectory;
    }

    /**
     * Get redis object - right
     * @param fileSystemObject  file system object name. Must not be null.
     * @param loginUser login user
     * @return redis object
     */
    public RightUserFileSystemObjectRedis getObj(String fileSystemObject, String loginUser) {
        Optional<RightUserFileSystemObjectRedis> rightUserFileSystemObjectRedis =
                rightUserFileSystemObjectRedisRepository
                        .findById(buildUniqueKey(fileSystemObject, loginUser));
        return rightUserFileSystemObjectRedis.orElse(null);
    }

    /**
     * Owner mapping when moving objects in Redis
     * <p>The current user is taken from the JWT token, Redis services need to know
     * the owner to not overwrite permissions</p>
     * @param loginUser owner user login
     * @param entities must not be {@literal null}. Must not contain {@literal null} elements.
     *                 <p>The class needs a <b>login</b> field and a corresponding <b>getters</b> and <b>setters</b></p>
     * @return objects with a set owner
     */
    public List<? extends BaseRedis> ownerMappingOnMove(String loginUser, @NotNull List<? extends BaseRedis> entities) {
        return entities.stream()
                .peek(entity -> entity.setLogin(loginUser))
                .collect(Collectors.toList());
    }

    /**
     * Sort file system objects by permissions.
     * @param fileSystemObjects file system objects.
     * @param right data right. Must not be {@literal null}.
     * @return sorted data.
     */
    @Override
    public List<String> permissionFiltering(List<String> fileSystemObjects, OperationRights right) {
        List<String> readyFileSystemObjects = new ArrayList<>();
        Iterable<FileRedis> fileRedis = getFileRedis(fileSystemObjects);
        Iterable<DirectoryRedis> directoryRedis = getDirectoryRedis(fileSystemObjects);

        fileRedis.forEach(obj -> {
            if ((obj.getUserLogins() != null && obj.getUserLogins().contains(getLoginUser())) ||
                    getLoginUser().equals(obj.getLogin())) {
                readyFileSystemObjects.add(obj.getSystemNameFile());
            }
        });
        directoryRedis.forEach(obj -> {
            if ((obj.getUserLogins() != null && obj.getUserLogins().contains(getLoginUser())) ||
                    getLoginUser().equals(obj.getLogin())) {
                readyFileSystemObjects.add(obj.getSystemNameDirectory());
            }
        });

        return readyFileSystemObjects;
    }

    /**
     * Getting information about files from Redis
     * @param fileSystemObjects file system objects
     * @return data redis file
     */
    private @NotNull Iterable<FileRedis> getFileRedis(@NotNull List<String> fileSystemObjects) {
        return fileRedisRepository.findAllById(fileSystemObjects);
    }

    /**
     * Getting information about directories from Redis
     * @param fileSystemObjects file system objects
     * @return data redis directory
     */
    private @NotNull Iterable<DirectoryRedis> getDirectoryRedis(List<String> fileSystemObjects) {
        return directoryRedisRepository.findAllById(fileSystemObjects);
    }

    /**
     * Sort data by operation
     * @param fileRedisList files information
     * @param operation type of transaction
     * @return sorted files data
     */
    private List<FileRedis> conductorOperationFileRedis(
            @NotNull List<FileRedis> fileRedisList, OperationRights operation
    ) {
        List<RightUserFileSystemObjectRedis> rightUserFileSystemObjectRedis = (List<RightUserFileSystemObjectRedis>)
                rightUserFileSystemObjectRedisRepository
                        .findAllById(fileRedisList.stream()
                                .map(obj -> buildUniqueKey(obj.getSystemNameFile(), getLoginUser()))
                                .collect(Collectors.toList()));

        return fileRedisList
                .stream()
                .filter(obj -> {
                    for (RightUserFileSystemObjectRedis right : rightUserFileSystemObjectRedis) {
                        if (right.getRight() == null) {
                            return false;
                        }
                        if (obj.getSystemNameFile().equals(unpackingUniqueKey(right.getFileSystemObject()))
                                && right.getRight().contains(operation)) {
                            return true;
                        }
                    }
                    return false;
                })
                .toList();
    }

    /**
     * Sort data by operation
     * @param directoryRedisList directory information
     * @param operation type of transaction
     * @return sorted directory data
     */
    private List<DirectoryRedis> conductorOperationDirectoryRedis(
            @NotNull List<DirectoryRedis> directoryRedisList,
            OperationRights operation
    ) {
        List<RightUserFileSystemObjectRedis> rightUserFileSystemObjectRedis = (List<RightUserFileSystemObjectRedis>)
                rightUserFileSystemObjectRedisRepository
                        .findAllById(directoryRedisList.stream()
                                .map(obj -> buildUniqueKey(obj.getSystemNameDirectory(), getLoginUser()))
                                .collect(Collectors.toList()));

        return directoryRedisList
                .stream()
                .filter(obj -> {
                    for (RightUserFileSystemObjectRedis right : rightUserFileSystemObjectRedis) {
                        if (obj.getSystemNameDirectory().equals(unpackingUniqueKey(right.getFileSystemObject()))
                                && right.getRight().contains(operation)) {
                            return true;
                        }
                    }
                    return false;
                })
                .toList();
    }

    /**
     * Set access claims (resources)
     * @param rawAccessToken <b>JWT</b> raw access token, example (string):
     * <pre>
     * Bearer: token...
     * </pre>
     */
    @Override
    public void setAccessClaims(String rawAccessToken) {
        this.accessClaims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(rawAccessToken));
        this.setLoginUser(accessClaims.get("login", String.class));
    }

    /**
     * Set access claims (resources)
     * @param accessClaims This is ultimately a JSON map and any values can be added to it, but JWT standard
     *                     names are provided as type-safe getters and setters for convenience.
     */
    @Override
    public void setAccessClaims(Claims accessClaims) {
        this.accessClaims = accessClaims;
    }

}
