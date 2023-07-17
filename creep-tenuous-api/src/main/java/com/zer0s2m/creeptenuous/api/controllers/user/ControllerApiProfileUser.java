package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiProfileUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataIsDeletingFileObjectApi;
import com.zer0s2m.creeptenuous.common.data.DataTransferredUserApi;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.http.ResponseUserApi;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.security.jwt.domain.JwtAuthentication;
import com.zer0s2m.creeptenuous.security.jwt.exceptions.messages.UserNotFoundMsg;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.utils.JwtUtils;
import com.zer0s2m.creeptenuous.services.user.ServiceProfileUser;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@V1APIRestController
public class ControllerApiProfileUser implements ControllerApiProfileUserDoc {

    private final JwtProvider jwtProvider;

    private final ServiceProfileUser serviceProfileUser;

    @Autowired
    public ControllerApiProfileUser(JwtProvider jwtProvider, ServiceProfileUser serviceProfileUser) {
        this.jwtProvider = jwtProvider;
        this.serviceProfileUser = serviceProfileUser;
    }

    /**
     * Get info user by JWT token
     * @return user info
     */
    @Override
    @GetMapping("/user/profile")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseUserApi profile(@RequestHeader(name = "Authorization") String accessToken) {
        Claims claimsAccess = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        JwtAuthentication userInfo = JwtUtils.generate(claimsAccess);
        User currentUser = serviceProfileUser.getUserByLogin(userInfo.getLogin());
        return new ResponseUserApi(currentUser.getLogin() ,currentUser.getEmail(),
                currentUser.getName(), Set.of(currentUser.getRole()));
    }

    /**
     * Set setting for user - deleting a user if it is deleted
     * @param data setting data
     * @param accessToken raw access JWT token
     * @throws UserNotFoundException not exists user
     */
    @Override
    @PatchMapping("/user/profile/settings/is-delete-file-objects")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void setIsDeletingFileObjectsSettings(
            final @Valid @RequestBody @NotNull DataIsDeletingFileObjectApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws UserNotFoundException {
        Claims claimsAccess = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        JwtAuthentication userInfo = JwtUtils.generate(claimsAccess);

        serviceProfileUser.setIsDeletingFileObjectSettings(userInfo.getLogin(), data.isDelete());
    }

    /**
     * Set setting - transfer file objects to designated user
     * @param data setting data
     * @param accessToken wat access JWT token
     * @throws UserNotFoundException not exists user
     */
    @Override
    @PatchMapping("/user/profile/settings/set-transfer-user")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void setTransferredUserId(
            final @Valid @RequestBody @NotNull DataTransferredUserApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws UserNotFoundException {
        Claims claimsAccess = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        JwtAuthentication userInfo = JwtUtils.generate(claimsAccess);

        serviceProfileUser.setTransferredUserSettings(userInfo.getLogin(), data.userId());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public UserNotFoundMsg handleExceptionNotIsExistsUser(@NotNull UserNotFoundException error) {
        return new UserNotFoundMsg(error.getMessage());
    }

}
