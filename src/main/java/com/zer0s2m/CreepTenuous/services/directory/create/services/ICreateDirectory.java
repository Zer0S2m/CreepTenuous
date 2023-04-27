package com.zer0s2m.CreepTenuous.services.directory.create.services;

import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceDirectoryRedis;
import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;
import io.jsonwebtoken.Claims;

import java.nio.file.*;
import java.util.List;

public interface ICreateDirectory extends IServiceDirectoryRedis {
    void create(List<String> parents, String nameDirectory) throws NoSuchFileException, FileAlreadyExistsException;

    default void checkDirectory(Path pathNewDirectory) throws FileAlreadyExistsException {
        if (Files.exists(pathNewDirectory)) {
            throw new FileAlreadyExistsException(Directory.DIRECTORY_EXISTS.get());
        }
    }

    default DirectoryRedis getObjRedis(Claims accessClaims, String nameDirectory, String pathDirectory) {
        String loginUser = accessClaims.get("login", String.class);
        String roleUser = accessClaims.get("role", String.class);

        return IServiceDirectoryRedis.createObj(loginUser, roleUser, nameDirectory, pathDirectory);
    }
}
