package com.zer0s2m.creeptenuous.services.redis.security;

import com.zer0s2m.creeptenuous.redis.exceptions.NoRightsRedisException;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
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

    private final RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    @Autowired
    public ServiceManagerRightsImpl(
            JwtProvider jwtProvider,
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository
    ) {
        this.jwtProvider = jwtProvider;
        this.directoryRedisRepository = directoryRedisRepository;
        this.fileRedisRepository = fileRedisRepository;
        this.rightUserFileSystemObjectRedisRepository = rightUserFileSystemObjectRedisRepository;
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
     */
    @Override
    public void addRight(RightUserFileSystemObjectRedis right) {
        rightUserFileSystemObjectRedisRepository.save(right);
    }

    /**
     * Set access token
     * @param accessToken <b>JWT</b> access token
     */
    @Override
    public void setAccessToken(String accessToken) {
        setAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        this.loginUser = accessClaims.get("login", String.class);
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
    protected void setAccessClaims(String rawAccessToken) {
        this.accessClaims = jwtProvider.getAccessClaims(rawAccessToken);
    }
}
