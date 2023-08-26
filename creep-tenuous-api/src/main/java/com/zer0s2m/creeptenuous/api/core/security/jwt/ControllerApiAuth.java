package com.zer0s2m.creeptenuous.api.core.security.jwt;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiAuthDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.exceptions.AccountIsBlockedException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotValidPasswordException;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionAccountIsBlockedMsg;
import com.zer0s2m.creeptenuous.redis.services.user.ServiceBlockUserRedis;
import com.zer0s2m.creeptenuous.security.jwt.exceptions.NoValidJwtRefreshTokenException;
import com.zer0s2m.creeptenuous.security.jwt.exceptions.messages.NoValidJwtRefreshTokenMsg;
import com.zer0s2m.creeptenuous.security.jwt.exceptions.messages.UserNotFoundMsg;
import com.zer0s2m.creeptenuous.security.jwt.exceptions.messages.UserNotValidPasswordMsg;
import com.zer0s2m.creeptenuous.security.jwt.http.JwtRefreshTokenRequest;
import com.zer0s2m.creeptenuous.security.jwt.http.JwtResponse;
import com.zer0s2m.creeptenuous.security.jwt.http.JwtUserRequest;
import com.zer0s2m.creeptenuous.security.jwt.services.JwtService;
import com.zer0s2m.creeptenuous.security.jwt.utils.JwtUtils;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@V1APIRestController
@CrossOrigin
public class ControllerApiAuth implements ControllerApiAuthDoc {

    private final JwtService jwtService;

    private final ServiceBlockUserRedis serviceBlockUserRedis;

    @Autowired
    public ControllerApiAuth(JwtService jwtService, ServiceBlockUserRedis serviceBlockUserRedis) {
        this.jwtService = jwtService;
        this.serviceBlockUserRedis = serviceBlockUserRedis;
    }

    /**
     * User authorization
     * @param user data for login
     * @return JWT tokens
     * @throws UserNotFoundException user not found
     * @throws UserNotValidPasswordException invalid password
     * @throws AccountIsBlockedException the account is blocked
     */
    @Override
    @PostMapping(value = "/auth/login")
    public JwtResponse login(final @Valid @RequestBody @NotNull JwtUserRequest user) throws UserNotFoundException,
            UserNotValidPasswordException, AccountIsBlockedException {
        if (serviceBlockUserRedis.check(user.login())) {
            throw new AccountIsBlockedException();
        }
        return jwtService.login(user);
    }

    /**
     * Get JWT access token
     * @param request JWT refresh token
     * @return JWT tokens
     * @throws UserNotFoundException user not found
     * @throws NoValidJwtRefreshTokenException invalid JWT refresh token
     */
    @Override
    @PostMapping(value = "/auth/token")
    public JwtResponse access(final @Valid @RequestBody @NotNull JwtRefreshTokenRequest request)
            throws UserNotFoundException, NoValidJwtRefreshTokenException {
        return jwtService.getAccessToken(request.refreshToken());
    }

    /**
     * Get JWT refresh token
     * @param request JWT refresh token
     * @return JWT tokens
     * @throws NoValidJwtRefreshTokenException invalid JWT refresh token
     * @throws UserNotFoundException user not found
     */
    @Override
    @PostMapping(value = "/auth/refresh")
    public JwtResponse refresh(final @Valid @RequestBody @NotNull JwtRefreshTokenRequest request)
            throws NoValidJwtRefreshTokenException, UserNotFoundException {
        return jwtService.getRefreshToken(request.refreshToken());
    }

    /**
     * Logout user
     * @param accessToken raw access JWT token
     */
    @Override
    @GetMapping("/auth/logout")
    public void logout(@RequestHeader(name = "Authorization") String accessToken) {
        jwtService.logout(JwtUtils.getPureAccessToken(accessToken));
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public UserNotFoundMsg handleExceptionNotIsExistsUser(@NotNull UserNotFoundException error) {
        return new UserNotFoundMsg(error.getMessage());
    }

    @ExceptionHandler(UserNotValidPasswordException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public UserNotValidPasswordMsg handleExceptionNotValidPasswordUser(@NotNull UserNotValidPasswordException error) {
        return new UserNotValidPasswordMsg(error.getMessage());
    }

    @ExceptionHandler(NoValidJwtRefreshTokenException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public NoValidJwtRefreshTokenMsg handleExceptionNotValidPasswordUser(
            @NotNull NoValidJwtRefreshTokenException error) {
        return new NoValidJwtRefreshTokenMsg(error.getMessage());
    }

    @ExceptionHandler(AccountIsBlockedException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public ExceptionAccountIsBlockedMsg handleExceptionAccountIsBlocked(@NotNull AccountIsBlockedException error) {
        return new ExceptionAccountIsBlockedMsg(error.getMessage());
    }

}
