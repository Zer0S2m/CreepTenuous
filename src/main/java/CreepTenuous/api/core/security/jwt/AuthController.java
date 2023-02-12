package CreepTenuous.api.core.security.jwt;

import CreepTenuous.api.core.security.jwt.messages.UserNotFoundMsg;
import CreepTenuous.api.core.security.jwt.messages.UserNotValidPasswordMsg;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.providers.jwt.http.JwtResponse;
import CreepTenuous.providers.jwt.http.JwtUserRequest;
import CreepTenuous.services.user.exceptions.UserNotFoundException;
import CreepTenuous.services.user.exceptions.UserNotValidPasswordException;
import CreepTenuous.services.user.security.jwt.impl.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@V1APIController
@CrossOrigin
public class AuthController {
    @Autowired
    private JwtService jwtService;

    @PostMapping(value = "/auth/login")
    public JwtResponse login(
            @RequestBody JwtUserRequest user
    ) throws UserNotFoundException, UserNotValidPasswordException {
        return jwtService.login(user);
    }

    @PostMapping(value = "/auth/token")
    public void access() {

    }

    @PostMapping(value = "/auth/refresh")
    public void refresh() {

    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public UserNotFoundMsg handleExceptionNotIsExistsUser(UserNotFoundException error) {
        return new UserNotFoundMsg(error.getMessage());
    }

    @ExceptionHandler(UserNotValidPasswordException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public UserNotValidPasswordMsg handleExceptionNotValidPasswordUser(UserNotValidPasswordException error) {
        return new UserNotValidPasswordMsg(error.getMessage());
    }
}
