package com.zer0s2m.creeptenuous.services.redis.security;

import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.redis.exceptions.AddRightsYourselfException;
import com.zer0s2m.creeptenuous.redis.exceptions.NoExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.redis.exceptions.NoRightsRedisException;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.JwtRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.RightUserFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    protected final DirectoryRedisRepository directoryRedisRepository;

    protected final FileRedisRepository fileRedisRepository;

    protected final JwtRedisRepository jwtRedisRepository;

    private final RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    @Autowired
    public ServiceManagerRightsImpl(
            JwtProvider jwtProvider,
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository,
            JwtRedisRepository jwtRedisRepository
    ) {
        this.jwtProvider = jwtProvider;
        this.directoryRedisRepository = directoryRedisRepository;
        this.fileRedisRepository = fileRedisRepository;
        this.rightUserFileSystemObjectRedisRepository = rightUserFileSystemObjectRedisRepository;
        this.jwtRedisRepository = jwtRedisRepository;
    }

    /**
     * Checking permissions to perform some action on a specific file object
     * @param operation type of transaction
     * @param fileSystemObjects file system objects
     * @throws NoRightsRedisException Insufficient rights to perform the operation
     */
    @Override
    public void checkRightByOperation(OperationRights operation, List<String> fileSystemObjects)
            throws NoRightsRedisException {
        List<FileRedis> fileRedis = conductorFileRedisByLogin(getFileRedis(fileSystemObjects));
        List<DirectoryRedis> directoryRedis = conductorDirectoryRedisByLogin(getDirectoryRedis(fileSystemObjects));

        List<FileRedis> fileRedisSorted = conductorOperationFileRedis(fileRedis, operation);
        List<DirectoryRedis> directoryRedisSorted = conductorOperationDirectoryRedis(directoryRedis, operation);
    }

    /**
     * Create a user right on a file system object
     * @param right Data right
     * @throws AddRightsYourselfException adding rights over the interaction of file system objects to itself
     */
    @Override
    public void addRight(RightUserFileSystemObjectRedis right) throws AddRightsYourselfException {
        checkAddingRightsYourself(right);
        Optional<RightUserFileSystemObjectRedis> existsRight = rightUserFileSystemObjectRedisRepository
                .findById(right.getFileSystemObject());
        if (existsRight.isPresent()) {
            RightUserFileSystemObjectRedis existsRightReady = existsRight.get();
            List<OperationRights> operationRightsList = existsRightReady.getRight();
            operationRightsList.addAll(right.getRight());

            existsRightReady.setRight(operationRightsList
                    .stream()
                    .distinct()
                    .collect(Collectors.toList())
            );

            rightUserFileSystemObjectRedisRepository.save(existsRightReady);
        } else {
            rightUserFileSystemObjectRedisRepository.save(right);
        }
    }

    /**
     * Checking for the existence of a file system object in the database
     * @param nameFileSystemObject The system name of the file system object
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     */
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
    public void isExistsUser(String loginUser) throws UserNotFoundException {
        boolean isExists = jwtRedisRepository.existsById(loginUser);
        if (!isExists) {
            throw new UserNotFoundException();
        }
    }

    /**
     * Checking for adding rights to itself
     * @param right must not be null.
     * @throws AddRightsYourselfException adding rights over the interaction of file system objects to itself
     */
    public void checkAddingRightsYourself(RightUserFileSystemObjectRedis right) throws AddRightsYourselfException {
        if (right.getLogin().equals(getLoginUser())) {
            throw new AddRightsYourselfException();
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
     * Sort data by login user
     * @param fileRedisData file data
     * @return sorted file data
     */
    private @NotNull List<FileRedis> conductorFileRedisByLogin(@NotNull Iterable<FileRedis> fileRedisData) {
        List<FileRedis> fileRedisList = new ArrayList<>();
        fileRedisData.forEach(fileRedis -> {
            if (fileRedis.getUserLogins().contains(loginUser)) {
                fileRedisList.add(fileRedis);
            }
        });
        return fileRedisList;
    }

    /**
     * Sort data by login user
     * @param directoryRedisData directory information
     * @return sorted directory data
     */
    private @NotNull List<DirectoryRedis> conductorDirectoryRedisByLogin(@NotNull Iterable<DirectoryRedis> directoryRedisData) {
        List<DirectoryRedis> directoryRedisList = new ArrayList<>();
        directoryRedisData.forEach(directoryRedis -> {
            if (directoryRedis.getUserLogins().contains(loginUser)) {
                directoryRedisList.add(directoryRedis);
            }
        });
        return directoryRedisList;
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
                                .map(FileRedis::getSystemNameFile)
                                .collect(Collectors.toList()));

        return fileRedisList
                .stream()
                .filter(obj -> {
                    for (RightUserFileSystemObjectRedis right : rightUserFileSystemObjectRedis) {
                        if (obj.getSystemNameFile().equals(right.getFileSystemObject())
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
                                .map(DirectoryRedis::getSystemNameDirectory)
                                .collect(Collectors.toList()));

        return directoryRedisList
                .stream()
                .filter(obj -> {
                    for (RightUserFileSystemObjectRedis right : rightUserFileSystemObjectRedis) {
                        if (obj.getSystemNameDirectory().equals(right.getFileSystemObject())
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
