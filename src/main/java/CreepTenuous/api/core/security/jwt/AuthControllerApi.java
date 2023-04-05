package CreepTenuous.api.core.security.jwt;

import CreepTenuous.providers.jwt.exceptions.NoValidJwtRefreshTokenException;
import CreepTenuous.providers.jwt.http.JwtRefreshTokenRequest;
import CreepTenuous.providers.jwt.exceptions.messages.NoValidJwtRefreshTokenMsg;
import CreepTenuous.providers.jwt.exceptions.messages.UserNotFoundMsg;
import CreepTenuous.providers.jwt.exceptions.messages.UserNotValidPasswordMsg;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.providers.jwt.http.JwtResponse;
import CreepTenuous.providers.jwt.http.JwtUserRequest;
import CreepTenuous.services.user.exceptions.UserNotFoundException;
import CreepTenuous.services.user.exceptions.UserNotValidPasswordException;
import CreepTenuous.providers.jwt.services.impl.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@V1APIController
@CrossOrigin
public class AuthControllerApi {
    private final JwtService jwtService;

    @Autowired
    public AuthControllerApi(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping(value = "/auth/login")
    public JwtResponse login(
            final @RequestBody JwtUserRequest user
    ) throws UserNotFoundException, UserNotValidPasswordException {
        return jwtService.login(user);
    }

    @PostMapping(value = "/auth/token")
    public JwtResponse access(
            final @RequestBody JwtRefreshTokenRequest request
    ) throws UserNotFoundException {
        return jwtService.getAccessToken(request.refreshToken());
    }

    @PostMapping(value = "/auth/refresh")
    public JwtResponse refresh(
            final @RequestBody JwtRefreshTokenRequest request
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
