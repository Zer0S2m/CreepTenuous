package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCategoryUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerCategoryFileSystemObject;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataUserCategory;
import com.zer0s2m.creeptenuous.common.data.DataControlFileSystemObjectInCategoryApi;
import com.zer0s2m.creeptenuous.common.data.DataCreateUserCategoryApi;
import com.zer0s2m.creeptenuous.common.data.DataDeleteUserCategoryApi;
import com.zer0s2m.creeptenuous.common.data.DataEditUserCategoryApi;
import com.zer0s2m.creeptenuous.common.exceptions.NoExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.utils.JwtUtils;
import com.zer0s2m.creeptenuous.services.user.ServiceCategoryUser;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@V1APIRestController
public class ControllerApiCategoryUser implements ControllerApiCategoryUserDoc {

    private final ServiceCategoryUser serviceCategoryUser;

    private final BaseServiceFileSystemRedisManagerRightsAccess baseServiceFileSystemRedis;

    private final JwtProvider jwtProvider;

    @Autowired
    public ControllerApiCategoryUser(
            ServiceCategoryUser serviceCategoryUser,
            BaseServiceFileSystemRedisManagerRightsAccess baseServiceFileSystemRedis,
            JwtProvider jwtProvider) {
        this.serviceCategoryUser = serviceCategoryUser;
        this.baseServiceFileSystemRedis = baseServiceFileSystemRedis;
        this.jwtProvider = jwtProvider;
    }

    @Override
    @GetMapping("/user/category")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ContainerDataUserCategory> getAll(@RequestHeader(name = "Authorization") String accessToken) {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        return serviceCategoryUser.getAll(claims.get("login", String.class));
    }

    /**
     * Creating a category for file objects
     * @param data data to create
     * @param accessToken raw access JWT token
     * @return entity data
     * @throws UserNotFoundException user not exists
     */
    @Override
    @PostMapping("/user/category")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ContainerDataUserCategory create(
            final @Valid @RequestBody @NotNull DataCreateUserCategoryApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws UserNotFoundException {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        return serviceCategoryUser.create(data.title(), claims.get("login", String.class));
    }

    /**
     * Update custom category by id
     * @param data data to update
     * @param accessToken raw access JWT token
     * @throws NotFoundException not found user category
     */
    @Override
    @PutMapping("/user/category")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void edit(
            final @Valid @RequestBody @NotNull DataEditUserCategoryApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws NotFoundException {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        serviceCategoryUser.edit(data.id(), data.title(),
                claims.get("login", String.class));
    }

    /**
     * Delete a custom category by its id
     * @param data data to delete
     * @param accessToken raw access JWT token
     * @throws NotFoundException not found user category
     */
    @Override
    @DeleteMapping("/user/category")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(
            final @Valid @RequestBody @NotNull DataDeleteUserCategoryApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws NotFoundException {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        serviceCategoryUser.delete(data.id(), claims.get("login", String.class));
    }

    /**
     * Bind a file object to a custom category
     * @param data binding data
     * @param accessToken raw access JWT token
     * @throws NotFoundException not found category or file system object
     */
    @Override
    @PostMapping("/user/category/file-system-object")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void setFileSystemObjectInCategory(
            final @Valid @RequestBody @NotNull DataControlFileSystemObjectInCategoryApi data,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws NotFoundException {
        prepareSetOrUnsetCategory(data, accessToken);

        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));

        serviceCategoryUser.setFileSystemObjectInCategory(
                data.categoryId(), data.fileSystemObject(), claims.get("login", String.class));
    }

    /**
     * Link a file object to a custom category
     * @param data data to unbind
     * @param accessToken raw access JWT token
     * @throws NotFoundException not found category or file system object
     */
    @Override
    @DeleteMapping("/user/category/file-system-object")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void unsetFileSystemObjectInCategory(
            final @Valid @RequestBody @NotNull DataControlFileSystemObjectInCategoryApi data,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws NotFoundException {
        prepareSetOrUnsetCategory(data, accessToken);

        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));

        serviceCategoryUser.unsetFileSystemObjectInCategory(
                data.categoryId(), data.fileSystemObject(), claims.get("login", String.class));
    }

    /**
     * Checks for permissions and the existence of a file object
     * @param data data for installation or removal
     * @param accessToken raw access JWT token
     * @throws NoExistsFileSystemObjectRedisException not found file system object
     */
    private void prepareSetOrUnsetCategory(
            @NotNull DataControlFileSystemObjectInCategoryApi data, String accessToken)
            throws NotFoundException {
        baseServiceFileSystemRedis.setAccessClaims(accessToken);
        baseServiceFileSystemRedis.setIsException(true);
        baseServiceFileSystemRedis.checkRights(data.fileSystemObject());
        if (!baseServiceFileSystemRedis.existsById(data.fileSystemObject())) {
            throw new NoExistsFileSystemObjectRedisException();
        }
    }

    /**
     * Get all objects of the file category associated with the user category by ID
     * @param categoryId ID custom category
     * @param accessToken raw access JWT token
     * @return linked file category objects to user category
     * @throws NotFoundException not found category
     */
    @Override
    @GetMapping("/user/category/file-system-object")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ContainerCategoryFileSystemObject> getFileSystemObjectInCategoryByCategoryId(
            final @RequestParam("categoryId") Long categoryId,
            @RequestHeader(name = "Authorization") String accessToken) throws NotFoundException {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));

        return serviceCategoryUser.getFileSystemObjectInCategoryByCategoryId(
                categoryId, claims.get("login", String.class));
    }

}
