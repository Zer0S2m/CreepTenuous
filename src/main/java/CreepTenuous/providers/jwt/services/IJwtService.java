package CreepTenuous.providers.jwt.services;

import CreepTenuous.providers.jwt.exceptions.NoValidJwtRefreshTokenException;
import CreepTenuous.providers.jwt.http.JwtResponse;
import CreepTenuous.providers.jwt.http.JwtUserRequest;
import CreepTenuous.services.user.exceptions.UserNotFoundException;
import CreepTenuous.services.user.exceptions.UserNotValidPasswordException;

public interface IJwtService {
    JwtResponse login(JwtUserRequest user) throws UserNotFoundException, UserNotValidPasswordException;

    JwtResponse getAccessToken(String refreshToken) throws UserNotFoundException;

    JwtResponse getRefreshToken(String refreshToken) throws NoValidJwtRefreshTokenException, UserNotFoundException;
}
