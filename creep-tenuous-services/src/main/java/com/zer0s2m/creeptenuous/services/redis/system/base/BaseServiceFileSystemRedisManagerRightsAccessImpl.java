package com.zer0s2m.creeptenuous.services.redis.system.base;

import com.zer0s2m.creeptenuous.redis.exceptions.NoRightsRedisException;
import com.zer0s2m.creeptenuous.redis.models.base.BaseRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Basic service for serving file system objects in Redis.
 * <ul>
 *     <li>Checking Ownership of an Object</li>
 * </ul>
 */
@Service("service-file-system-redis")
public class BaseServiceFileSystemRedisManagerRightsAccessImpl
        implements BaseServiceFileSystemRedisManagerRightsAccess {

    private boolean isException = true;

    protected final DirectoryRedisRepository directoryRedisRepository;

    protected final FileRedisRepository fileRedisRepository;

    protected final JwtProvider jwtProvider;

    protected Claims accessClaims;

    private Boolean enableCheckIsNameDirectory = false;

    private Boolean resetCheckIsNameDirectory = false;

    private String loginUser;

    @Autowired
    public BaseServiceFileSystemRedisManagerRightsAccessImpl(DirectoryRedisRepository directoryRedisRepository,
                                                             FileRedisRepository fileRedisRepository, JwtProvider jwtProvider) {
        this.directoryRedisRepository = directoryRedisRepository;
        this.fileRedisRepository = fileRedisRepository;
        this.jwtProvider = jwtProvider;
    }

    /**
     * Validate right user (directories)
     * @param parents Real names directory
     * @param systemParents System names directory
     * @param nameDirectory System name directory
     * @return are there existing rights
     * @throws NoRightsRedisException When the user has no execution rights
     */
    @Override
    public boolean checkRights(List<String> parents, List<String> systemParents, String nameDirectory)
            throws NoRightsRedisException {
        if (enableCheckIsNameDirectory && nameDirectory != null) {
            systemParents.add(nameDirectory);
        }
        if (resetCheckIsNameDirectory) {
            setEnableCheckIsNameDirectory(false);
        }
        Iterable<DirectoryRedis> directoryRedisIterable = directoryRedisRepository.findAllById(systemParents);

        return check(directoryRedisIterable);
    }

    /**
     * Validate right user (directories)
     * @param parents Real names directory
     * @param systemParents System names directory
     * @param nameDirectory System name directory
     * @param isException whether to raise an exception if there are no rights
     * @return are there existing rights
     */
    @Override
    public boolean checkRights(List<String> parents, List<String> systemParents,
                               String nameDirectory, boolean isException) {
        if (isException) {
            checkRights(parents, systemParents, nameDirectory);
        } else {
            try {
                checkRights(parents, systemParents, nameDirectory);
            } catch (NoRightsRedisException ignored) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validate right user (files and directories)
     * @param systemName system name file system object
     * @return is rights
     * @throws NoRightsRedisException When the user has no execution right
     */
    @Override
    public boolean checkRights(List<String> systemName)
            throws NoRightsRedisException {
        Iterable<FileRedis> fileRedisIterable = fileRedisRepository.findAllById(systemName);
        Iterable<DirectoryRedis> directoryRedisIterable = directoryRedisRepository.findAllById(systemName);
        boolean isRightFiles = check(fileRedisIterable);
        boolean isRightDirectory = check(directoryRedisIterable);

        return isRightFiles == isRightDirectory;
    }

    /**
     * Validate right user
     * @param entity entities must not be {@literal null} nor must it contain {@literal null}.
     * @return is rights
     */
    private boolean check(@NotNull Iterable<? extends BaseRedis> entity) {
        AtomicBoolean isRight = new AtomicBoolean(true);
        entity.forEach((objRedis) -> {
            if (!Objects.equals(objRedis.getLogin(), getLoginUser())) {
                isRight.set(false);
                if (getIsException()) {
                    throw new NoRightsRedisException();
                }
            }
        });
        return isRight.get();
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
     *
     * @return login user
     */
    @Override
    public String getLoginUser() {
        return this.loginUser;
    }

    /**
     * Setting login user
     *
     * @param loginUser login user
     */
    @Override
    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
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
     *
     * @param accessClaims This is ultimately a JSON map and any values can be added to it, but JWT standard
     *                     names are provided as type-safe getters and setters for convenience.
     */
    @Override
    public void setAccessClaims(Claims accessClaims) {
        this.accessClaims = accessClaims;
        this.loginUser = accessClaims.get("login", String.class);
    }

    /**
     * Enable check right inclusive name directory
     * @param enableCheckIsNameDirectory is enabled
     */
    public void setEnableCheckIsNameDirectory(Boolean enableCheckIsNameDirectory) {
        this.enableCheckIsNameDirectory = enableCheckIsNameDirectory;
    }

    /**
     * Reset parameter - {@link BaseServiceFileSystemRedisManagerRightsAccessImpl#enableCheckIsNameDirectory}
     * @param resetCheckIsNameDirectory is enabled
     */
    public void setResetCheckIsNameDirectory(Boolean resetCheckIsNameDirectory) {
        this.resetCheckIsNameDirectory = resetCheckIsNameDirectory;
    }

    /**
     * Setting whether to raise an exception
     * @param isException whether to raise an exception if there are no rights
     */
    public void setIsException(boolean isException) {
        this.isException = isException;
    }

    /**
     * Getting whether to raise an exception
     * @return whether to raise an exception if there are no rights
     */
    public boolean getIsException() {
        return this.isException;
    }

}
