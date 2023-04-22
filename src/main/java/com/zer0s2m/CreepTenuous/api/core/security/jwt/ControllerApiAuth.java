package com.zer0s2m.CreepTenuous.api.core.security.jwt;

import com.zer0s2m.CreepTenuous.providers.jwt.exceptions.NoValidJwtRefreshTokenException;
import com.zer0s2m.CreepTenuous.providers.jwt.http.JwtRefreshTokenRequest;
import com.zer0s2m.CreepTenuous.providers.jwt.exceptions.messages.NoValidJwtRefreshTokenMsg;
import com.zer0s2m.CreepTenuous.providers.jwt.exceptions.messages.UserNotFoundMsg;
import com.zer0s2m.CreepTenuous.providers.jwt.exceptions.messages.UserNotValidPasswordMsg;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.providers.jwt.http.JwtResponse;
import com.zer0s2m.CreepTenuous.providers.jwt.http.JwtUserRequest;
import com.zer0s2m.CreepTenuous.services.user.exceptions.UserNotFoundException;
import com.zer0s2m.CreepTenuous.services.user.exceptions.UserNotValidPasswordException;
import com.zer0s2m.CreepTenuous.providers.jwt.services.impl.JwtService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@V1APIController
@CrossOrigin
public class ControllerApiAuth {
    private final JwtService jwtService;

    @Autowired
    public ControllerApiAuth(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping(value = "/auth/login")
    public JwtResponse login(
            final @Valid @RequestBody JwtUserRequest user
    ) throws UserNotFoundException, UserNotValidPasswordException {
        return jwtService.login(user);
    }

    @PostMapping(value = "/auth/token")
    public JwtResponse access(
            final @Valid @RequestBody JwtRefreshTokenRequest request
    ) throws UserNotFoundException, NoValidJwtRefreshTokenException {
        return jwtService.getAccessToken(request.refreshToken());
    }

    @PostMapping(value = "/auth/refresh")
    public JwtResponse refresh(
            final @Valid @RequestBody JwtRefreshTokenRequest request
    ) throws NoValidJwtRefreshTokenException, UserNotFoundException {
        return jwtService.getRefreshToken(request.refreshToken());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public UserNotFoundMsg handleExceptionNotIsExistsUser(UserNotFoundException error) {
        return new UserNotFoundMsg(error.getMessage());
    }

    @ExceptionHandler(UserNotValidPasswordException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public UserNotValidPasswordMsg handleExceptionNotValidPasswordUser(UserNotValidPasswordException error) {
        return new UserNotValidPasswordMsg(error.getMessage());
    }

    @ExceptionHandler(NoValidJwtRefreshTokenException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public NoValidJwtRefreshTokenMsg handleExceptionNotValidPasswordUser(NoValidJwtRefreshTokenException error) {
        return new NoValidJwtRefreshTokenMsg(error.getMessage());
    }
}
