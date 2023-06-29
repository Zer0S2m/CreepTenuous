package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiControlUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataBlockUserApi;
import com.zer0s2m.creeptenuous.common.data.DataDeleteUserApi;
import com.zer0s2m.creeptenuous.common.exceptions.BlockingSelfUserException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionBlockingSelfUserMsg;
import com.zer0s2m.creeptenuous.common.http.ResponseUserApi;
import com.zer0s2m.creeptenuous.events.UserEventPublisher;
import com.zer0s2m.creeptenuous.security.jwt.domain.JwtAuthentication;
import com.zer0s2m.creeptenuous.security.jwt.exceptions.messages.UserNotFoundMsg;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.utils.JwtUtils;
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

    private final UserEventPublisher userEventPublisher;

    private final JwtProvider jwtProvider;

    @Autowired
    public ControllerApiControlUser(ServiceControlUser serviceControlUser, UserEventPublisher userEventPublisher,
                                    JwtProvider jwtProvider) {
        this.serviceControlUser = serviceControlUser;
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
    @RolesAllowed("ROLE_ADMIN")
    public List<ResponseUserApi> getAllUsers() {
        return serviceControlUser.getAllUsers()
                .stream()
                .map(user -> new ResponseUserApi(user.getLogin(), user.getEmail(),
                        user.getName(), Set.of(user.getRole())))
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
    public void deleteUserByLogin(final @Valid @RequestBody @NotNull DataDeleteUserApi data)
            throws UserNotFoundException {
        serviceControlUser.deleteUser(data.login());
        userEventPublisher.publishDelete(data.login());
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
            final @Valid @RequestBody @NotNull DataBlockUserApi data,
            @RequestHeader(name = "Authorization") String accessToken)
            throws UserNotFoundException, BlockingSelfUserException {
        Claims claimsAccess = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        JwtAuthentication userInfo = JwtUtils.generate(claimsAccess);
        if (userInfo.getLogin().equals(data.login())) {
            throw new BlockingSelfUserException();
        }
        serviceControlUser.blockUser(data.login());
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
    public void unblockUser(final @Valid @RequestBody @NotNull DataBlockUserApi data) throws UserNotFoundException {
        serviceControlUser.unblockUser(data.login());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public UserNotFoundMsg handleExceptionNotIsExistsUser(@NotNull UserNotFoundException error) {
        return new UserNotFoundMsg(error.getMessage());
    }

    @ExceptionHandler(BlockingSelfUserException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionBlockingSelfUserMsg handleExceptionBlockingSelfUser(@NotNull BlockingSelfUserException error) {
        return new ExceptionBlockingSelfUserMsg(error.getMessage());
    }

}
