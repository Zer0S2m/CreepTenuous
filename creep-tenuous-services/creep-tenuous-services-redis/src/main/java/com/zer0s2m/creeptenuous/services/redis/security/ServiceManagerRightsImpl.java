package com.zer0s2m.creeptenuous.services.redis.security;

import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.containers.ContainerGrantedRight;
import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.common.enums.ManagerRights;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.ChangeRightsYourselfException;
import com.zer0s2m.creeptenuous.common.exceptions.NoExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.common.exceptions.NoExistsRightException;
import com.zer0s2m.creeptenuous.common.exceptions.NoRightsRedisException;
import com.zer0s2m.creeptenuous.common.http.ResponseGrantedRightsApi;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.models.base.BaseRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.JwtRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.RightUserFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.redis.services.resources.ServiceRedisManagerResources;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.utils.JwtUtils;
import com.zer0s2m.creeptenuous.common.utils.WalkDirectoryInfo;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service for managing user rights for interacting with a target file system object
 */
@Service("service-manager-rights")
public class ServiceManagerRightsImpl implements ServiceManagerRights {

    /**
     * Basic operation in the system to manage user rights
     */
    static final OperationRights baseOperationRights = OperationRights.SHOW;

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

    private final RootPath rootPath;

    @Autowired
    public ServiceManagerRightsImpl(
            JwtProvider jwtProvider,
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository,
            JwtRedisRepository jwtRedisRepository,
            ServiceRedisManagerResources serviceRedisManagerResources,
            RootPath rootPath
    ) {
        this.jwtProvider = jwtProvider;
        this.directoryRedisRepository = directoryRedisRepository;
        this.fileRedisRepository = fileRedisRepository;
        this.rightUserFileSystemObjectRedisRepository = rightUserFileSystemObjectRedisRepository;
        this.jwtRedisRepository = jwtRedisRepository;
        this.serviceRedisManagerResources = serviceRedisManagerResources;
        this.rootPath = rootPath;
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

        List<FileRedis> fileRedis = serviceRedisManagerResources.getResourceFileRedis(fileSystemObjects);
        List<DirectoryRedis> directoryRedis = serviceRedisManagerResources.getResourceDirectoryRedis(
                fileSystemObjects);

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
        List<DirectoryRedis> directoryRedisCurrent = serviceRedisManagerResources.getResourceDirectoryRedis(
                List.of(fileSystemObject));

        if (getIsDirectory() && directoryRedisCurrent.size() != 0 && directoryRedisCurrent.get(0).getIsDirectory()) {
            List<ContainerInfoFileSystemObject> attached = WalkDirectoryInfo.walkDirectory(
                    Path.of(directoryRedisCurrent.get(0).getPathDirectory()));
            List<String> namesFileSystemObject = attached
                    .stream()
                    .map(ContainerInfoFileSystemObject::nameFileSystemObject)
                    .toList();
            List<DirectoryRedis> directoryRedisResource = serviceRedisManagerResources.getResourceDirectoryRedis(
                    namesFileSystemObject);
            List<FileRedis> fileRedisResource = serviceRedisManagerResources.getResourceFileRedis(
                    namesFileSystemObject);

            List<DirectoryRedis> directoryRedisSorted = conductorOperationDirectoryRedis(directoryRedisResource,
                    operation);
            List<FileRedis> fileRedisSorted = conductorOperationFileRedis(fileRedisResource, operation);

            if (!((fileRedisSorted.size() + directoryRedisSorted.size()) == namesFileSystemObject.size())) {
                throw new NoRightsRedisException();
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
     * Create a user right on a file system object
     * @param right data right. must not be {@literal null} nor must it contain {@literal null}.
     * @param operationRights type of transaction. Must not be null.
     * @throws ChangeRightsYourselfException Change rights over the interaction of file system objects to itself
     */
    @Override
    public void addRight(final @NotNull List<RightUserFileSystemObjectRedis> right, OperationRights operationRights)
            throws ChangeRightsYourselfException {
        List<String> ids = new ArrayList<>();
        List<String> idsFileSystemObject = new ArrayList<>();

        for (RightUserFileSystemObjectRedis obj : right) {
            checkAddingRightsYourself(obj);
            final String key = buildUniqueKey(obj.getFileSystemObject(), obj.getLogin());
            idsFileSystemObject.add(obj.getFileSystemObject());
            obj.setFileSystemObject(key);
            ids.add(key);
        }

        Iterable<RightUserFileSystemObjectRedis> rightUserFileSystemObjectExists =
                rightUserFileSystemObjectRedisRepository.findAllById(ids);
        final HashMap<String, List<OperationRights>> operationRightsExistsItinerary = new HashMap<>();
        StreamSupport.stream(rightUserFileSystemObjectExists.spliterator(), false)
                .forEach(obj -> operationRightsExistsItinerary.put(obj.getFileSystemObject(), obj.getRight()));

        final List<DirectoryRedis> directoryRedisList = serviceRedisManagerResources.getResourceDirectoryRedis(
                idsFileSystemObject);
        final List<FileRedis> fileRedisList = serviceRedisManagerResources.getResourceFileRedis(
                idsFileSystemObject);

        directoryRedisList.forEach(directoryRedis ->
                setDataUserLoginsToRedisObj(directoryRedis, right.get(0).getLogin()));
        fileRedisList.forEach(fileRedis -> setDataUserLoginsToRedisObj(fileRedis, right.get(0).getLogin()));

        right.forEach(obj -> {
            List<OperationRights> operationRightsExists;
            if (operationRightsExistsItinerary.containsKey(obj.getFileSystemObject())) {
                operationRightsExists = new ArrayList<>(
                        operationRightsExistsItinerary.get(obj.getFileSystemObject()));
            } else {
                operationRightsExists = new ArrayList<>();
            }

            operationRightsExists.add(operationRights);
            obj.setRight(operationRightsExists
                    .stream()
                    .distinct()
                    .collect(Collectors.toList()));
        });

        rightUserFileSystemObjectRedisRepository.saveAll(right);
        directoryRedisRepository.saveAll(directoryRedisList);
        fileRedisRepository.saveAll(fileRedisList);
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
     * Set directory pass access if the nesting level prevents the user from reaching the file system object
     * @param nameFileSystemObject filesystem object system name. Must not be {@literal null}.
     * @param loginUser login user. Must not be {@literal null}.
     * @throws ChangeRightsYourselfException Change rights over the interaction of file system objects to itself
     */
    @Override
    public void setDirectoryPassAccess(String nameFileSystemObject, String loginUser)
            throws ChangeRightsYourselfException {
        Optional<FileRedis> fileRedisOptional = fileRedisRepository.findById(nameFileSystemObject);
        Optional<DirectoryRedis> directoryRedisOptional = directoryRedisRepository.findById(nameFileSystemObject);

        if (fileRedisOptional.isPresent()) {
            setDirectoryPassAccessAddRights(nameFileSystemObject,
                    Path.of(fileRedisOptional.get().getPathFile()), loginUser);
        }
        if (directoryRedisOptional.isPresent()) {
            setDirectoryPassAccessAddRights(nameFileSystemObject,
                    Path.of(directoryRedisOptional.get().getPathDirectory()), loginUser);
        }
    }

    /**
     * Set directory pass access if the nesting level prevents the user from reaching the file system object
     * @param nameFileSystemObject filesystem object system name. Must not be {@literal null}.
     * @param source source file system object
     * @param loginUser login user. Must not be {@literal null}.
     * @throws ChangeRightsYourselfException Change rights over the interaction of file system objects to itself
     */
    private void setDirectoryPassAccessAddRights(String nameFileSystemObject, Path source, String loginUser)
            throws ChangeRightsYourselfException {
        if (Files.exists(source)) {
            String sourceStr = source.toString()
                    .replace(rootPath.getRootPath() + Directory.SEPARATOR.get(), "")
                    .replace(Directory.SEPARATOR.get() + nameFileSystemObject, "");
            List<String> systemParents = new ArrayList<>(List.of(sourceStr.split(Directory.SEPARATOR.get())));
            if (systemParents.size() > 0) {
                addRight(buildObj(systemParents, loginUser, baseOperationRights),
                        baseOperationRights);
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
    @Override
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
     * Delete a user rights a file system object
     * @param right data right. must not be {@literal null} nor must it contain {@literal null}.
     * @param operationRights type of transaction. Must not be null.
     * @throws ChangeRightsYourselfException Change rights over the interaction of file system objects to itself
     * @throws NoExistsRightException The right was not found in the database.
     *                                Or is {@literal null} {@link NullPointerException}
     */
    @Override
    public void deleteRight(@NotNull List<RightUserFileSystemObjectRedis> right, OperationRights operationRights)
            throws ChangeRightsYourselfException, NoExistsRightException {
        for (RightUserFileSystemObjectRedis obj : right) {
            checkDeletingRightsYourself(obj);
        }

        List<String> idsToDelete = new ArrayList<>();

        List<RightUserFileSystemObjectRedis> cleanRights = right
                .stream()
                .filter(obj -> obj.getRight() != null && obj.getRight().contains(operationRights))
                .toList();

        cleanRights.forEach(obj -> {
            List<OperationRights> operationRightsList = obj.getRight();
            if (operationRightsList.size() == 1) {
                idsToDelete.add(obj.getFileSystemObject());
            } else {
                operationRightsList.remove(operationRights);
                obj.setRight(operationRightsList);
            }
        });

        List<String> idsFileSystemObject = idsToDelete
                .stream()
                .map(this::unpackingUniqueKey)
                .toList();

        if (right.size() >= 1) {
            List<DirectoryRedis> directoryRedisList = deleteUserLoginFromRedisFileSystemObjects(
                    serviceRedisManagerResources
                            .getResourceDirectoryRedis(idsFileSystemObject),
                    right.get(0).getLogin());
            List<FileRedis> fileRedisList = deleteUserLoginFromRedisFileSystemObjects(
                    serviceRedisManagerResources
                            .getResourceFileRedis(idsFileSystemObject),
                    right.get(0).getLogin());

            directoryRedisRepository.saveAll(directoryRedisList);
            fileRedisRepository.saveAll(fileRedisList);
        }

        rightUserFileSystemObjectRedisRepository.deleteAllById(idsToDelete);
        rightUserFileSystemObjectRedisRepository.saveAll(cleanRights);
    }

    /**
     * Removing a User Binding to an Object in Redis
     * @param entities data right. must not be {@literal null} nor must it contain {@literal null}.
     * @param userLogin username to be deleted
     * @return clean entities
     * @param <E> file object type
     */
    private <E extends BaseRedis> List<E> deleteUserLoginFromRedisFileSystemObjects(
            @NotNull List<E> entities, String userLogin) {
        return entities
                .stream()
                .filter(obj -> obj.getUserLogins() != null && obj.getUserLogins().contains(userLogin))
                .peek(obj -> {
                    List<String> userLogins = obj.getUserLogins();
                    userLogins.remove(userLogin);
                    obj.setUserLogins(userLogins);
                })
                .toList();
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
     * Get all granted rights to the specified object
     * @param systemName file system object
     * @return granted rights
     */
    @Override
    public List<ContainerGrantedRight> getGrantedRight(final String systemName) {
        DirectoryRedis directoryRedis = serviceRedisManagerResources.getResourceDirectoryRedis(
                systemName);
        FileRedis fileRedis = serviceRedisManagerResources.getResourceFileRedis(systemName);
        if (directoryRedis != null) {
            return getGrantedRight(directoryRedis, directoryRedis.getSystemNameDirectory());
        } else if (fileRedis != null) {
            return getGrantedRight(fileRedis, fileRedis.getSystemNameFile());
        }

        return new ArrayList<>();
    }

    /**
     * Get all granted rights to the specified object
     * @param object underlying file storage object
     * @param systemName name file system object
     * @return granted rights
     */
    private @NotNull List<ContainerGrantedRight> getGrantedRight(@NotNull BaseRedis object, String systemName) {
        List<String> userLogins = object.getUserLogins();
        List<String> idsUserRights = new ArrayList<>();

        if (userLogins != null) {
            userLogins.forEach(userLogin ->
                    idsUserRights.add(buildUniqueKey(systemName, userLogin)));
        }

        Iterable<RightUserFileSystemObjectRedis> rightUserFileSystemObjectRedisList =
                rightUserFileSystemObjectRedisRepository.findAllById(idsUserRights);
        List<ContainerGrantedRight> containerGrantedRightList = new ArrayList<>();
        rightUserFileSystemObjectRedisList.forEach(right ->
                containerGrantedRightList.add(new ContainerGrantedRight(
                        right.getLogin(), right.getRight())));

        return containerGrantedRightList;
    }

    /**
     * Get information about all issued rights to all objects
     * @return granted all rights
     */
    @Override
    public Collection<ResponseGrantedRightsApi> getGrantedRight() {
        String userLogin = getLoginUser();

        List<DirectoryRedis> directoryRedisList = serviceRedisManagerResources.getResourceDirectoryRedisByLoginUser(
                userLogin);
        List<FileRedis> fileRedisList = serviceRedisManagerResources.getResourceFileRedisByLoginUser(
                userLogin);

        Set<String> idsRights = new HashSet<>();
        directoryRedisList.forEach(directoryRedis ->
                idsRights.addAll(getIdsRights(directoryRedis, directoryRedis.getSystemNameDirectory())));
        fileRedisList.forEach(fileRedis ->
                idsRights.addAll(getIdsRights(fileRedis, fileRedis.getSystemNameFile())));

        Map<String, ResponseGrantedRightsApi> mapRights = new HashMap<>();
        Iterable<RightUserFileSystemObjectRedis> rightUserFileSystemObjectRedisIterable =
                rightUserFileSystemObjectRedisRepository.findAllById(idsRights);
        rightUserFileSystemObjectRedisIterable.forEach(right -> {
            final String systemName = unpackingUniqueKey(right.getFileSystemObject());
            if (!mapRights.containsKey(systemName)) {
                mapRights.put(systemName, new ResponseGrantedRightsApi(
                        systemName, List.of(new ContainerGrantedRight(
                        right.getLogin(), right.getRight()
                ))
                ));
            } else {
                final List<ContainerGrantedRight> lastRights = new ArrayList<>(mapRights.get(systemName).rights());
                lastRights.add(new ContainerGrantedRight(right.getLogin(), right.getRight()));
                mapRights.put(systemName, new ResponseGrantedRightsApi(
                        systemName, lastRights));
            }
        });

        return mapRights.values();
    }

    /**
     * Get unique user permission keys to interact with file objects
     * @param object underlying file storage object
     * @param systemName name file system object
     * @return ids
     */
    private @NotNull List<String> getIdsRights(@NotNull BaseRedis object, String systemName) {
        List<String> idsRights = new ArrayList<>();
        List<String> userLogins = object.getUserLogins();

        if (userLogins != null) {
            userLogins.forEach(login ->
                    idsRights.add(systemName + ManagerRights.SEPARATOR_UNIQUE_KEY.get() + login));
        }

        return idsRights;
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
    @Override
    public RightUserFileSystemObjectRedis getObj(String fileSystemObject, String loginUser) {
        Optional<RightUserFileSystemObjectRedis> rightUserFileSystemObjectRedis =
                rightUserFileSystemObjectRedisRepository
                        .findById(buildUniqueKey(fileSystemObject, loginUser));
        return rightUserFileSystemObjectRedis.orElse(null);
    }

    /**
     * Get redis object - right
     * @param fileSystemObject must not be {@literal null}. Must not contain {@literal null} elements.
     * @param loginUser owner user login
     * @return redis object
     */
    @Override
    public List<RightUserFileSystemObjectRedis> getObj(@NotNull List<String> fileSystemObject, String loginUser) {
        List<String> ids = fileSystemObject
                .stream()
                .map(obj -> buildUniqueKey(obj, loginUser))
                .toList();
        return StreamSupport
                .stream(rightUserFileSystemObjectRedisRepository.findAllById(ids).spliterator(), false)
                .toList();
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
    @Override
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
        Iterable<FileRedis> fileRedis = serviceRedisManagerResources.getResourceFileRedis(fileSystemObjects);
        Iterable<DirectoryRedis> directoryRedis = serviceRedisManagerResources.getResourceDirectoryRedis(
                fileSystemObjects);

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
                    if (obj.getLogin().equals(getLoginUser())) {
                        return true;
                    }
                    for (RightUserFileSystemObjectRedis right : rightUserFileSystemObjectRedis) {
                        if (right.getRight() == null) {
                            return false;
                        }
                        if ((obj.getSystemNameFile().equals(unpackingUniqueKey(right.getFileSystemObject()))
                                && right.getRight().contains(operation))) {
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
                    if (obj.getLogin().equals(getLoginUser())) {
                        return true;
                    }
                    for (RightUserFileSystemObjectRedis right : rightUserFileSystemObjectRedis) {
                        if (right.getRight() == null) {
                            return false;
                        }
                        if ((obj.getSystemNameDirectory().equals(unpackingUniqueKey(right.getFileSystemObject()))
                                && right.getRight().contains(operation))) {
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
