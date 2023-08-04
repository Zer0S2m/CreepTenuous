package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiShortcutFileSystemObjectsUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataControlShortcutFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundCommentFileSystemObjectException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;
import com.zer0s2m.creeptenuous.security.jwt.domain.JwtAuthentication;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.utils.JwtUtils;
import com.zer0s2m.creeptenuous.services.user.ServiceShortcutFileSystemObjectsUser;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@V1APIRestController
public class ControllerApiShortcutFileSystemObjectsUser implements ControllerApiShortcutFileSystemObjectsUserDoc {

    private final JwtProvider jwtProvider;

    private final BaseServiceFileSystemRedisManagerRightsAccess baseServiceFileSystemRedis;

    private final ServiceShortcutFileSystemObjectsUser serviceShortcutFileSystemObjectsUser;

    @Autowired
    public ControllerApiShortcutFileSystemObjectsUser(
            JwtProvider jwtProvider,
            BaseServiceFileSystemRedisManagerRightsAccess baseServiceFileSystemRedis,
            ServiceShortcutFileSystemObjectsUser serviceShortcutFileSystemObjectsUser) {
        this.jwtProvider = jwtProvider;
        this.baseServiceFileSystemRedis = baseServiceFileSystemRedis;
        this.serviceShortcutFileSystemObjectsUser = serviceShortcutFileSystemObjectsUser;
    }

    /**
     * Create a shortcut to a file object
     * @param data data to create
     * @param accessToken raw access JWT token
     * @throws UserNotFoundException user not exists
     * @throws NotFoundCommentFileSystemObjectException file object not exists
     */
    @Override
    @PostMapping("/common/shortcut/file-system-object")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void create(
            @Valid @RequestBody @NotNull final DataControlShortcutFileSystemObjectApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws UserNotFoundException,
            NotFoundCommentFileSystemObjectException {
        baseServiceFileSystemRedis.setAccessClaims(accessToken);
        baseServiceFileSystemRedis.setIsException(true);
        baseServiceFileSystemRedis.checkRights(List.of(
                data.attachedFileSystemObject(), data.toAttachedFileSystemObject()));
        if (!baseServiceFileSystemRedis.existsById(data.attachedFileSystemObject()) &&
            !baseServiceFileSystemRedis.existsById(data.toAttachedFileSystemObject())) {
            throw new NotFoundCommentFileSystemObjectException();
        }

        Claims claimsAccess = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        JwtAuthentication userInfo = JwtUtils.generate(claimsAccess);

        serviceShortcutFileSystemObjectsUser.create(
                userInfo.getLogin(),
                UUID.fromString(data.attachedFileSystemObject()),
                UUID.fromString(data.toAttachedFileSystemObject()));
    }

    /**
     * Delete a shortcut to a file object
     * @param data data to delete
     * @param accessToken raw access JWT token
     * @throws UserNotFoundException user not exists
     */
    @Override
    @DeleteMapping("/common/shortcut/file-system-object")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(
            @Valid @RequestBody @NotNull final DataControlShortcutFileSystemObjectApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws UserNotFoundException {
        Claims claimsAccess = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        JwtAuthentication userInfo = JwtUtils.generate(claimsAccess);

        serviceShortcutFileSystemObjectsUser.delete(
                userInfo.getLogin(),
                UUID.fromString(data.attachedFileSystemObject()),
                UUID.fromString(data.toAttachedFileSystemObject()));
    }

}
