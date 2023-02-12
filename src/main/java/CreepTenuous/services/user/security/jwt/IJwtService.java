package CreepTenuous.services.user.security.jwt;

import CreepTenuous.providers.jwt.http.JwtResponse;
import CreepTenuous.providers.jwt.http.JwtUserRequest;
import CreepTenuous.services.user.exceptions.UserNotFoundException;
import CreepTenuous.services.user.exceptions.UserNotValidPasswordException;

public interface IJwtService {
    JwtResponse login(JwtUserRequest user) throws UserNotFoundException, UserNotValidPasswordException;
    void access();
    void refresh();
}
