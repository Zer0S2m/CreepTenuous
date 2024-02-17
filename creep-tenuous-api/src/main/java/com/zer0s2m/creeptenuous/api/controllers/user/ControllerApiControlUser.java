package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiControlUserDoc;
import com.zer0s2m.creeptenuous.api.annotation.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataControlUserApi;
import com.zer0s2m.creeptenuous.common.data.DataBlockUserTemporarilyApi;
import com.zer0s2m.creeptenuous.common.exceptions.BlockingSelfUserException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.http.ResponseError;
import com.zer0s2m.creeptenuous.common.http.ResponseUserApi;
import com.zer0s2m.creeptenuous.events.UserEventPublisher;
import com.zer0s2m.creeptenuous.redis.services.user.ServiceBlockUserRedis;
import com.zer0s2m.creeptenuous.security.jwt.JwtAuthentication;
import com.zer0s2m.creeptenuous.security.jwt.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.JwtUtils;
import com.zer0s2m.creeptenuous.services.user.ServiceControlUser;
import io.jsonwebtoken.Claims;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@V1APIRestController
public class ControllerApiControlUser implements ControllerApiControlUserDoc {

    private final ServiceControlUser serviceControlUser;

    private final ServiceBlockUserRedis serviceBlockUserRedis;

    private final UserEventPublisher userEventPublisher;

    private final JwtProvider jwtProvider;

    @Autowired
    public ControllerApiControlUser(
            ServiceControlUser serviceControlUser,
            ServiceBlockUserRedis serviceBlockUserRedis,
            UserEventPublisher userEventPublisher,
            JwtProvider jwtProvider) {
        this.serviceControlUser = serviceControlUser;
        this.serviceBlockUserRedis = serviceBlockUserRedis;
        this.userEventPublisher = userEventPublisher;
        this.jwtProvider = jwtProvider;
    }

    /**
     * Get all users in the system
     * @return users
     */
    @Override
    @GetMapping("/user/control/list")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ResponseUserApi> getAllUsers() {
        return serviceControlUser.getAllUsers()
                .stream()
                .map(user -> {
                    String avatar = user.getAvatar();
                    if (avatar != null) {
                        String[] avatarSplit = avatar.split("/");
                        avatar = "avatars/" + avatarSplit[avatarSplit.length - 1];
                    }

                    return new ResponseUserApi(
                            user.getLogin(),
                            user.getEmail(),
                            user.getName(),
                            Set.of(user.getRole()),
                            null,
                            null,
                            !user.isAccountNonLocked(),
                            serviceBlockUserRedis.check(user.getLogin()),
                            avatar
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Removing a user from the system by his login
     * @param data data to delete
     * @throws UserNotFoundException user does not exist in the system
     */
    @Override
    @DeleteMapping("/user/control/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @RolesAllowed("ROLE_ADMIN")
    public void deleteUserByLogin(final @Valid @RequestBody @NotNull DataControlUserApi data)
            throws UserNotFoundException {
        userEventPublisher.publishDelete(data.login());
        serviceControlUser.deleteUser(data.login());
    }

    /**
     * Block a user by his login
     * @param data data to block
     * @param accessToken raw JWT access token
     * @throws UserNotFoundException user does not exist in the system
     * @throws BlockingSelfUserException Blocking self users
     */
    @Override
    @PatchMapping("/user/control/block")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @RolesAllowed("ROLE_ADMIN")
    public void blockUser(
            final @Valid @RequestBody @NotNull DataControlUserApi data,
            @RequestHeader(name = "Authorization") String accessToken)
            throws UserNotFoundException, BlockingSelfUserException {
        checkBLockingSelfUser(accessToken, data.login());
        serviceControlUser.blockUser(data.login());
    }

    /**
     * Blocking a user by his login for a while
     * @param data data to block
     * @param accessToken raw JWT access token
     * @throws UserNotFoundException user does not exist in the system
     * @throws BlockingSelfUserException blocking self user
     */
    @Override
    @PostMapping("/user/control/block-temporarily")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @RolesAllowed("ROLE_ADMIN")
    public void blockUserTemporarily(
            final @Valid @RequestBody @NotNull DataBlockUserTemporarilyApi data,
            @RequestHeader(name = "Authorization") String accessToken)
            throws UserNotFoundException, BlockingSelfUserException {
        serviceControlUser.blockUserTemporarily(data.login(), data.fromDate(), data.toDate());
        checkBLockingSelfUser(accessToken, data.login());
    }

    /**
     * Unblock a user by his login
     * @param data data to unblock
     * @throws UserNotFoundException Blocking self users
     */
    @Override
    @PatchMapping("/user/control/unblock")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @RolesAllowed("ROLE_ADMIN")
    public void unblockUser(final @Valid @RequestBody @NotNull DataControlUserApi data) throws UserNotFoundException {
        serviceControlUser.unblockUser(data.login());
    }

    @ExceptionHandler(BlockingSelfUserException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseError handleExceptionBlockingSelfUser(@NotNull BlockingSelfUserException error) {
        return new ResponseError(error.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Check if a user is blocked on himself
     * @param accessToken raw JWT access token
     * @param loginUser login user
     * @throws BlockingSelfUserException Blocking self users
     */
    private void checkBLockingSelfUser(String accessToken, String loginUser) throws BlockingSelfUserException {
        Claims claimsAccess = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        JwtAuthentication userInfo = JwtUtils.generate(claimsAccess);
        if (userInfo.getLogin().equals(loginUser)) {
            throw new BlockingSelfUserException();
        }
    }

}
