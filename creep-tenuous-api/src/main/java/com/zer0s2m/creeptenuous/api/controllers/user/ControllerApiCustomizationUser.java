package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCustomizationUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataControlFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.data.DataCreateUserColorDirectoryApi;
import com.zer0s2m.creeptenuous.common.exceptions.NoExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundUserColorDirectoryException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.utils.JwtUtils;
import com.zer0s2m.creeptenuous.services.user.ServiceCustomizationUser;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@V1APIRestController
public class ControllerApiCustomizationUser implements ControllerApiCustomizationUserDoc {

    private final ServiceCustomizationUser serviceCustomizationUser;

    private final BaseServiceFileSystemRedisManagerRightsAccess baseServiceFileSystemRedis;

    private final JwtProvider jwtProvider;

    public ControllerApiCustomizationUser(
            ServiceCustomizationUser serviceCustomizationUser,
            BaseServiceFileSystemRedisManagerRightsAccess baseServiceFileSystemRedis,
            JwtProvider jwtProvider) {
        this.serviceCustomizationUser = serviceCustomizationUser;
        this.baseServiceFileSystemRedis = baseServiceFileSystemRedis;
        this.jwtProvider = jwtProvider;
    }

    /**
     * Set color for directory
     * @param data data to create
     * @param accessToken raw access JWT token
     * @throws UserNotFoundException user not found
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     */
    @Override
    @PutMapping("/user/customization/directory/color")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void setColorInDirectory(
            final @Valid @RequestBody @NotNull DataCreateUserColorDirectoryApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws NotFoundException {
        prepareRunActionColorInDirectory(data.fileSystemObject(), accessToken);

        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        serviceCustomizationUser.setColorInDirectory(
                data.fileSystemObject(), claims.get("login", String.class), data.color());
    }

    /**
     * Remove color from custom directory
     * @param data data to delete
     * @param accessToken raw access JWT token
     * @throws NotFoundUserColorDirectoryException color scheme not found for user directory
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     */
    @Override
    @DeleteMapping("/user/customization/directory/color")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteColorInDirectory(
            final @Valid @RequestBody @NotNull DataControlFileSystemObjectApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws NotFoundException {
        prepareRunActionColorInDirectory(data.fileSystemObject(), accessToken);

        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        serviceCustomizationUser.deleteColorInDirectory(
                data.fileSystemObject(), claims.get("login", String.class));
    }

    /**
     * Checks for permissions and the existence of a file object
     * @param fileSystemObject system name directory
     * @param accessToken raw access JWT token
     * @throws NoExistsFileSystemObjectRedisException not found file system object
     */
    private void prepareRunActionColorInDirectory(
            @NotNull String fileSystemObject, String accessToken)
            throws NotFoundException {
        baseServiceFileSystemRedis.setAccessClaims(accessToken);
        baseServiceFileSystemRedis.setIsException(true);
        baseServiceFileSystemRedis.checkRights(fileSystemObject);
        if (!baseServiceFileSystemRedis.existsById(fileSystemObject)) {
            throw new NoExistsFileSystemObjectRedisException();
        }
    }

}
