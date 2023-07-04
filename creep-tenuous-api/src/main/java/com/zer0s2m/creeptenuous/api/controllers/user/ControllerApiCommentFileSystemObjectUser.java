package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCommentFileSystemObjectUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataCreateCommentFileSystemObjectApi;
import com.zer0s2m.creeptenuous.models.common.CommentFileSystemObject;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.utils.JwtUtils;
import com.zer0s2m.creeptenuous.services.user.ServiceCommentFileSystemObject;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

@V1APIRestController
public class ControllerApiCommentFileSystemObjectUser implements ControllerApiCommentFileSystemObjectUserDoc {

    private final ServiceCommentFileSystemObject serviceCommentFileSystemObject;

    private final JwtProvider jwtProvider;

    @Autowired
    public ControllerApiCommentFileSystemObjectUser(
            ServiceCommentFileSystemObject serviceCommentFileSystemObject,
            JwtProvider jwtProvider) {
        this.serviceCommentFileSystemObject = serviceCommentFileSystemObject;
        this.jwtProvider = jwtProvider;
    }

    /**
     * Create a comment for a file object
     * @param data data to created
     * @param accessToken access JWT token
     * @return comment
     */
    @Override
    @PostMapping("/common/comment/file-system-object")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommentFileSystemObject create(
            final @Valid @RequestBody @NotNull DataCreateCommentFileSystemObjectApi data,
            final @RequestHeader(name = "Authorization") String accessToken) {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        return serviceCommentFileSystemObject.create(
                data.comment(), data.fileSystemObject(), claims.get("login", String.class));
    }

    @Override
    public void delete() {

    }

    @Override
    public void edit() {

    }

}
