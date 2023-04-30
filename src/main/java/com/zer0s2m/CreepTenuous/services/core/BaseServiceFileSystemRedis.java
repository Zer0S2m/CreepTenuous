package com.zer0s2m.CreepTenuous.services.core;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.jwt.utils.JwtUtils;
import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.base.IBaseServiceFileSystemRedis;
import com.zer0s2m.CreepTenuous.services.directory.create.exceptions.NoRightsCreateDirectoryException;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BaseServiceFileSystemRedis implements IBaseServiceFileSystemRedis {
    protected final DirectoryRedisRepository directoryRedisRepository;

    protected final JwtProvider jwtProvider;

    protected Claims accessClaims;

    private Boolean enableCheckIsNameDirectory = false;

    @Autowired
    protected BaseServiceFileSystemRedis(DirectoryRedisRepository directoryRedisRepository, JwtProvider jwtProvider) {
        this.directoryRedisRepository = directoryRedisRepository;
        this.jwtProvider = jwtProvider;
    }

    /**
     * Validate right user (directories)
     * @param parents Real names directory
     * @param systemParents System names directory
     * @param nameDirectory System name directory
     * @throws NoRightsCreateDirectoryException When the user has no execution rights
     */
    @Override
    public void checkRights(List<String> parents, List<String> systemParents, String nameDirectory)
            throws NoRightsCreateDirectoryException {
        String loginUser = accessClaims.get("login", String.class);

        if (enableCheckIsNameDirectory && nameDirectory != null) {
            systemParents.add(nameDirectory);
        }
        Iterable<DirectoryRedis> objsRedis = directoryRedisRepository.findAllById(systemParents);

        objsRedis.forEach((objRedis) -> {
            if (!Objects.equals(objRedis.getLogin(), loginUser)) {
                throw new NoRightsCreateDirectoryException();
            }
        });
    }

    public void setAccessToken(String accessToken) {
        setAccessClaims(JwtUtils.getPureAccessToken(accessToken));
    }

    protected void setAccessClaims(String rawAccessToken) {
        this.accessClaims = jwtProvider.getAccessClaims(rawAccessToken);
    }

    public void setEnableCheckIsNameDirectory(Boolean enableCheckIsNameDirectory) {
        this.enableCheckIsNameDirectory = enableCheckIsNameDirectory;
    }
}
