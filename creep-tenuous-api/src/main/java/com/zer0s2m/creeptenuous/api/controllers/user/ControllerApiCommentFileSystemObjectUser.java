package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCommentFileSystemObjectUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataCreateCommentFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.data.DataControlAnyObjectApi;
import com.zer0s2m.creeptenuous.common.data.DataEditCommentFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundCommentFileSystemObjectException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.models.common.CommentFileSystemObject;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.utils.JwtUtils;
import com.zer0s2m.creeptenuous.services.user.ServiceCommentFileSystemObject;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@V1APIRestController
public class ControllerApiCommentFileSystemObjectUser implements ControllerApiCommentFileSystemObjectUserDoc {

    private final ServiceCommentFileSystemObject serviceCommentFileSystemObject;

    private final BaseServiceFileSystemRedisManagerRightsAccess baseServiceFileSystemRedis;

    private final JwtProvider jwtProvider;

    @Autowired
    public ControllerApiCommentFileSystemObjectUser(
            ServiceCommentFileSystemObject serviceCommentFileSystemObject,
            BaseServiceFileSystemRedisManagerRightsAccess baseServiceFileSystemRedis,
            JwtProvider jwtProvider) {
        this.serviceCommentFileSystemObject = serviceCommentFileSystemObject;
        this.baseServiceFileSystemRedis = baseServiceFileSystemRedis;
        this.jwtProvider = jwtProvider;
    }

    /**
     * Get all comments of a file object for a user
     * @param fileSystemObject name file system object
     * @param accessToken access JWT token
     * @return comments
     * @throws NotFoundException not found comments for filesystem objects
     */
    @Override
    @GetMapping("/common/comment/file-system-object")
    @ResponseStatus(code = HttpStatus.OK)
    public List<CommentFileSystemObject> list(
            final @RequestParam("file") String fileSystemObject,
            final @RequestHeader(name = "Authorization") String accessToken) throws NotFoundException {
        baseServiceFileSystemRedis.setAccessClaims(accessToken);
        baseServiceFileSystemRedis.setIsException(true);
        baseServiceFileSystemRedis.checkRights(fileSystemObject);
        if (!baseServiceFileSystemRedis.existsById(fileSystemObject)) {
            throw new NotFoundCommentFileSystemObjectException();
        }

        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        return serviceCommentFileSystemObject.list(fileSystemObject, claims.get("login", String.class));
    }

    /**
     * Create a comment for a file object
     * @param data data to created
     * @param accessToken access JWT token
     * @return comment
     * @throws NotFoundException not found comments for filesystem objects
     */
    @Override
    @PostMapping("/common/comment/file-system-object")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommentFileSystemObject create(
            final @Valid @RequestBody @NotNull DataCreateCommentFileSystemObjectApi data,
            final @RequestHeader(name = "Authorization") String accessToken) throws NotFoundException {
        baseServiceFileSystemRedis.setAccessClaims(accessToken);
        baseServiceFileSystemRedis.setIsException(true);
        baseServiceFileSystemRedis.checkRights(data.fileSystemObject());
        if (!baseServiceFileSystemRedis.existsById(data.fileSystemObject())) {
            throw new NotFoundCommentFileSystemObjectException();
        }

        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        return serviceCommentFileSystemObject.create(
                data.comment(), data.fileSystemObject(), data.parentId(), claims.get("login", String.class));
    }

    /**
     * Delete a comment for a file object
     * @param data data to deleting
     * @param accessToken access JWT token
     * @throws NotFoundException not found comments for filesystem objects
     */
    @Override
    @DeleteMapping("/common/comment/file-system-object")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(
            final @Valid @RequestBody @NotNull DataControlAnyObjectApi data,
            final @RequestHeader(name = "Authorization") String accessToken)
            throws NotFoundException {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        serviceCommentFileSystemObject.delete(data.id(), claims.get("login", String.class));
    }

    /**
     * Edit a comment for a file object
     * @param data data to editing
     * @param accessToken access JWT token
     * @return comment
     * @throws NotFoundException not found comments for filesystem objects
     */
    @Override
    @PutMapping("/common/comment/file-system-object")
    @ResponseStatus(code = HttpStatus.OK)
    public CommentFileSystemObject edit(
            final @Valid @RequestBody @NotNull DataEditCommentFileSystemObjectApi data,
            final @RequestHeader(name = "Authorization") String accessToken)
            throws NotFoundException {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        return serviceCommentFileSystemObject.edit(
                data.comment(), data.id(), claims.get("login", String.class));
    }

}
