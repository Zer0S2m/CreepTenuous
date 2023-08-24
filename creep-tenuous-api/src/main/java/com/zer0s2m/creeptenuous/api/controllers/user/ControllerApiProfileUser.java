package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiProfileUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataControlFileObjectsExclusionApi;
import com.zer0s2m.creeptenuous.common.data.DataIsDeletingFileObjectApi;
import com.zer0s2m.creeptenuous.common.data.DataTransferredUserApi;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.http.ResponseUserApi;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.models.user.UserSettings;
import com.zer0s2m.creeptenuous.redis.services.resources.ServiceRedisManagerResources;
import com.zer0s2m.creeptenuous.security.jwt.domain.JwtAuthentication;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.utils.JwtUtils;
import com.zer0s2m.creeptenuous.services.user.ServiceProfileUser;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@V1APIRestController
public class ControllerApiProfileUser implements ControllerApiProfileUserDoc {

    private final JwtProvider jwtProvider;

    private final ServiceProfileUser serviceProfileUser;

    private final ServiceRedisManagerResources serviceRedisManagerResources;

    @Autowired
    public ControllerApiProfileUser(
            JwtProvider jwtProvider,
            ServiceProfileUser serviceProfileUser,
            ServiceRedisManagerResources serviceRedisManagerResources) {
        this.jwtProvider = jwtProvider;
        this.serviceProfileUser = serviceProfileUser;
        this.serviceRedisManagerResources = serviceRedisManagerResources;
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
        UserSettings userSettings = currentUser.getUserSettings();
        User transferredUser = userSettings != null ? userSettings.getTransferredUser() : null;

        return new ResponseUserApi(
                currentUser.getLogin(),
                currentUser.getEmail(),
                currentUser.getName(),
                Set.of(currentUser.getRole()),
                userSettings == null ? false : userSettings.getIsDeletingFileObjects(),
                transferredUser != null ? transferredUser.getLogin() : null
        );
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
     * @param accessToken raw access JWT token
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

    /**
     * Set file objects to exclusions when deleting a user and then allocating them
     * @param data data to set
     * @param accessToken raw access JWT token
     * @throws UserNotFoundException not exists user
     * @throws NotFoundException not exists file objects
     */
    @Override
    @PostMapping("/user/profile/settings/exclusions-file-objects")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void setFileObjectsExclusion(
            final @Valid @RequestBody @NotNull DataControlFileObjectsExclusionApi data,
            @RequestHeader(name = "Authorization") String accessToken)
            throws NotFoundException {
        Claims claimsAccess = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        JwtAuthentication userInfo = JwtUtils.generate(claimsAccess);

        final List<String > systemNames = data.fileObjects()
                .stream()
                .distinct()
                .toList();

        long countFileRedis = serviceRedisManagerResources.checkIfFilesRedisExistBySystemNamesAndUserLogin(
                systemNames, userInfo.getLogin()
        );
        long countDirectoryRedis = serviceRedisManagerResources.checkIfDirectoryRedisExistBySystemNamesAndUserLogin(
                systemNames, userInfo.getLogin());

        if (countFileRedis + countDirectoryRedis < systemNames.size()) {
            throw new NotFoundException("Not found file objects");
        }

        serviceProfileUser.setFileObjectsExclusion(
                systemNames
                        .stream()
                        .map(UUID::fromString)
                        .collect(Collectors.toList()),
                userInfo.getLogin());
    }

    /**
     * Remove file objects from exclusion on user deletion and then allocate them
     * @param data data to deleted
     * @param accessToken raw access JWT token
     * @throws UserNotFoundException not exists user
     */
    @Override
    @DeleteMapping("/user/profile/settings/exclusions-file-objects")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteFileObjectsExclusion(
            final @Valid @RequestBody @NotNull DataControlFileObjectsExclusionApi data,
            @RequestHeader(name = "Authorization") String accessToken)
            throws NotFoundException {
        Claims claimsAccess = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        JwtAuthentication userInfo = JwtUtils.generate(claimsAccess);

        serviceProfileUser.deleteFileObjectsExclusion(
                data.fileObjects()
                        .stream()
                        .distinct()
                        .map(UUID::fromString)
                        .collect(Collectors.toList()),
                userInfo.getLogin());
    }

}
