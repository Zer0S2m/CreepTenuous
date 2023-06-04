package com.zer0s2m.creeptenuous.services.redis.system.base;

import com.zer0s2m.creeptenuous.redis.exceptions.NoRightsRedisException;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedis;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BaseServiceFileSystemRedisImpl implements BaseServiceFileSystemRedis {

    protected final DirectoryRedisRepository directoryRedisRepository;

    protected final FileRedisRepository fileRedisRepository;

    protected final JwtProvider jwtProvider;

    protected Claims accessClaims;

    private Boolean enableCheckIsNameDirectory = false;

    private Boolean resetCheckIsNameDirectory = false;

    @Autowired
    public BaseServiceFileSystemRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider
    ) {
        this.directoryRedisRepository = directoryRedisRepository;
        this.fileRedisRepository = fileRedisRepository;
        this.jwtProvider = jwtProvider;
    }

    /**
     * Validate right user (directories)
     * @param parents Real names directory
     * @param systemParents System names directory
     * @param nameDirectory System name directory
     * @throws NoRightsRedisException When the user has no execution rights
     */
    @Override
    public void checkRights(List<String> parents, List<String> systemParents, String nameDirectory)
            throws NoRightsRedisException {
        String loginUser = accessClaims.get("login", String.class);

        if (enableCheckIsNameDirectory && nameDirectory != null) {
            systemParents.add(nameDirectory);
        }
        if (resetCheckIsNameDirectory) {
            setEnableCheckIsNameDirectory(false);
        }
        Iterable<DirectoryRedis> objsRedis = directoryRedisRepository.findAllById(systemParents);

        objsRedis.forEach((objRedis) -> {
            if (!Objects.equals(objRedis.getLogin(), loginUser)) {
                throw new NoRightsRedisException();
            }
        });
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
     * Validate right user (files)
     * @param systemNameFiles system names files
     * @throws NoRightsRedisException When the user has no execution right
     */
    @Override
    public void checkRights(List<String> systemNameFiles)
            throws NoRightsRedisException {
        String loginUser = accessClaims.get("login", String.class);

        Iterable<FileRedis> objsRedis = fileRedisRepository.findAllById(systemNameFiles);

        objsRedis.forEach((objRedis) -> {
            if (!Objects.equals(objRedis.getLogin(), loginUser)) {
                throw new NoRightsRedisException();
            }
        });
    }

    /**
     * Set access token
     * @param accessToken <b>JWT</b> access token
     */
    public void setAccessToken(String accessToken) {
        setAccessClaims(JwtUtils.getPureAccessToken(accessToken));
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

    /**
     * Enable check right inclusive name directory
     * @param enableCheckIsNameDirectory is enabled
     */
    public void setEnableCheckIsNameDirectory(Boolean enableCheckIsNameDirectory) {
        this.enableCheckIsNameDirectory = enableCheckIsNameDirectory;
    }

    /**
     * Reset parameter - {@link BaseServiceFileSystemRedisImpl#enableCheckIsNameDirectory}
     * @param resetCheckIsNameDirectory is enabled
     */
    public void setResetCheckIsNameDirectory(Boolean resetCheckIsNameDirectory) {
        this.resetCheckIsNameDirectory = resetCheckIsNameDirectory;
    }
}
