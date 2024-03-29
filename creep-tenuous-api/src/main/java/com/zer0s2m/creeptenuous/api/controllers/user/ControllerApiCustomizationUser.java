package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCustomizationUserDoc;
import com.zer0s2m.creeptenuous.api.annotation.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerCustomColorApi;
import com.zer0s2m.creeptenuous.common.data.*;
import com.zer0s2m.creeptenuous.common.exceptions.*;
import com.zer0s2m.creeptenuous.common.http.ResponseError;
import com.zer0s2m.creeptenuous.redis.services.resources.ServiceRedisManagerResources;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;
import com.zer0s2m.creeptenuous.security.jwt.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.JwtUtils;
import com.zer0s2m.creeptenuous.services.user.ServiceCustomizationUser;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@V1APIRestController
public class ControllerApiCustomizationUser implements ControllerApiCustomizationUserDoc {

    private final ServiceCustomizationUser serviceCustomizationUser;

    private final BaseServiceFileSystemRedisManagerRightsAccess baseServiceFileSystemRedis;

    private final ServiceRedisManagerResources serviceRedisManagerResources;

    private final JwtProvider jwtProvider;

    public ControllerApiCustomizationUser(
            ServiceCustomizationUser serviceCustomizationUser,
            BaseServiceFileSystemRedisManagerRightsAccess baseServiceFileSystemRedis,
            ServiceRedisManagerResources serviceRedisManagerResources,
            JwtProvider jwtProvider) {
        this.serviceCustomizationUser = serviceCustomizationUser;
        this.baseServiceFileSystemRedis = baseServiceFileSystemRedis;
        this.serviceRedisManagerResources = serviceRedisManagerResources;
        this.jwtProvider = jwtProvider;
    }

    /**
     * Set color for directory
     * @param data data to create
     * @param accessToken raw access JWT token
     * @throws UserNotFoundException user not found
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database
     * @throws FileObjectIsNotDirectoryTypeException file object is not a directory type
     */
    @Override
    @PutMapping("/user/customization/directory/color")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void setColorInDirectory(
            final @Valid @RequestBody @NotNull DataCreateUserColorDirectoryApi data,
            @RequestHeader(name = "Authorization") String accessToken)
            throws NotFoundException, FileObjectIsNotDirectoryTypeException {
        prepareRunActionColorInDirectory(data.fileSystemObject(), accessToken);

        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        serviceCustomizationUser.setColorInDirectory(
                data.fileSystemObject(), claims.get("login", String.class), data.userColorId());
    }

    /**
     * Remove color from custom directory
     * @param data data to delete
     * @param accessToken raw access JWT token
     * @throws NotFoundUserColorDirectoryException color scheme not found for user directory
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database
     * @throws FileObjectIsNotDirectoryTypeException file object is not a directory type
     */
    @Override
    @DeleteMapping("/user/customization/directory/color")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteColorInDirectory(
            final @Valid @RequestBody @NotNull DataControlFileSystemObjectApi data,
            @RequestHeader(name = "Authorization") String accessToken)
            throws NotFoundException, FileObjectIsNotDirectoryTypeException {
        prepareRunActionColorInDirectory(data.fileSystemObject(), accessToken);

        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        serviceCustomizationUser.deleteColorInDirectory(
                data.fileSystemObject(), claims.get("login", String.class));
    }

    /**
     * Set color scheme binding to custom category
     *
     * @param data        data to created
     * @param accessToken raw access JWT token
     * @throws NotFoundUserCategoryException not found the user category
     * @throws NotFoundUserColorException    not found user color entity
     * @throws UserNotFoundException         not found user color
     */
    @Override
    @PutMapping("/user/customization/category/color")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void setColorInCustomCategory(
            final @Valid @RequestBody @NotNull DataControlUserColorCategoryApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws NotFoundException {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        serviceCustomizationUser.setColorInCategory(
                claims.get("login", String.class), data.userColorId(), data.userCategoryId());
    }

    /**
     * Delete color scheme binding to custom category
     *
     * @param data        data to deleted
     * @param accessToken raw access JWT token
     * @throws NotFoundUserColorCategoryException custom category color scheme binding not found
     */
    @Override
    @DeleteMapping("/user/customization/category/color")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteColorInCustomCategory(
            final @Valid @RequestBody @NotNull DataControlUserColorCategoryApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws NotFoundException {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        serviceCustomizationUser.deleteColorInCategory(
                claims.get("login", String.class), data.userColorId(), data.userCategoryId());
    }

    /**
     * Get all custom colors
     * @param accessToken raw access JWT token
     * @return entities user colors
     */
    @Override
    @GetMapping("/user/customization/color")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ContainerCustomColorApi> getCustomColor(@RequestHeader(name = "Authorization") String accessToken) {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        return serviceCustomizationUser.getColors(claims.get("login", String.class));
    }

    /**
     * Create custom color
     * @param data data to created
     * @param accessToken raw access JWT token
     * @throws UserNotFoundException not found user
     */
    @Override
    @PostMapping("/user/customization/color")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void createCustomColor(
            final @Valid @RequestBody @NotNull ContainerCustomColorApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws NotFoundException {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        serviceCustomizationUser.createColor(claims.get("login", String.class), data.color());
    }

    /**
     * Edit custom color
     * @param data data to editing
     * @param accessToken raw access JWT token
     * @throws NotFoundUserColorException not found user color
     */
    @Override
    @PutMapping("/user/customization/color")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void editCustomColor(
            final @Valid @RequestBody @NotNull DataEditCustomColorApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws NotFoundException {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        serviceCustomizationUser.editColor(
                claims.get("login", String.class),
                data.id(),
                data.color());
    }

    /**
     * Delete custom color
     * @param data data to deleting
     * @param accessToken raw access JWT token
     * @throws NotFoundUserColorException not found user color
     */
    @Override
    @DeleteMapping("/user/customization/color")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCustomColor(
            final @Valid @RequestBody @NotNull DataControlAnyObjectApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws NotFoundException {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        serviceCustomizationUser.deleteColor(claims.get("login", String.class), data.id());
    }

    /**
     * Checks for permissions and the existence of a file object
     * @param fileSystemObject system name directory
     * @param accessToken raw access JWT token
     * @throws NoExistsFileSystemObjectRedisException not found file system object
     * @throws FileObjectIsNotDirectoryTypeException file object is not a directory type
     */
    private void prepareRunActionColorInDirectory(
            @NotNull String fileSystemObject, String accessToken)
            throws NotFoundException, FileObjectIsNotDirectoryTypeException {
        baseServiceFileSystemRedis.setAccessClaims(accessToken);
        baseServiceFileSystemRedis.setIsException(true);
        baseServiceFileSystemRedis.checkRights(fileSystemObject);
        if (!baseServiceFileSystemRedis.existsById(fileSystemObject)) {
            throw new NoExistsFileSystemObjectRedisException();
        }
        if (!serviceRedisManagerResources.checkFileObjectDirectoryType(fileSystemObject)) {
            throw new FileObjectIsNotDirectoryTypeException();
        }
    }

    @ExceptionHandler(FileObjectIsNotDirectoryTypeException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseError handleExceptionFileObjectIsNotDirectoryType(
            @NotNull FileObjectIsNotDirectoryTypeException error) {
        return new ResponseError(error.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

}
