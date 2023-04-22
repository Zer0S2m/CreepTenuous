package com.zer0s2m.CreepTenuous.providers.jwt.services;

import com.zer0s2m.CreepTenuous.providers.jwt.exceptions.NoValidJwtRefreshTokenException;
import com.zer0s2m.CreepTenuous.providers.jwt.http.JwtResponse;
import com.zer0s2m.CreepTenuous.providers.jwt.http.JwtUserRequest;
import com.zer0s2m.CreepTenuous.services.user.exceptions.UserNotFoundException;
import com.zer0s2m.CreepTenuous.services.user.exceptions.UserNotValidPasswordException;

public interface IJwtService {
    JwtResponse login(JwtUserRequest user) throws UserNotFoundException, UserNotValidPasswordException;

    JwtResponse getAccessToken(String refreshToken) throws UserNotFoundException, NoValidJwtRefreshTokenException;

    JwtResponse getRefreshToken(String refreshToken) throws NoValidJwtRefreshTokenException, UserNotFoundException;
}
