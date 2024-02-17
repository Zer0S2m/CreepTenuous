package com.zer0s2m.creeptenuous.security.jwt;

import com.zer0s2m.creeptenuous.common.exceptions.AccountIsBlockedException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotValidPasswordException;

/**
 * Service for maintenance of JW tokens
 */
public interface JwtService {

    /**
     * Login user in system
     * @param user data request user
     * @return tokens for login
     * @throws UserNotFoundException not user in system
     * @throws UserNotValidPasswordException invalid password
     * @throws AccountIsBlockedException the account is blocked
     */
    JwtResponse login(JwtUserRequest user) throws UserNotFoundException,
            UserNotValidPasswordException, AccountIsBlockedException;

    /**
     * Generate access JWT token
     * @param refreshToken refresh token
     * @return access token
     * @throws UserNotFoundException not user in system
     * @throws NoValidJwtRefreshTokenException invalid refresh token
     */
    JwtResponse getAccessToken(String refreshToken) throws UserNotFoundException, NoValidJwtRefreshTokenException;

    /**
     * Generate refresh JWT token
     * @param refreshToken refresh token
     * @return refresh token
     * @throws NoValidJwtRefreshTokenException invalid refresh token
     * @throws UserNotFoundException not user in system
     */
    JwtResponse getRefreshToken(String refreshToken) throws NoValidJwtRefreshTokenException, UserNotFoundException;

    /**
     * User logout
     * @param accessToken access JWT token
     */
    void logout(String accessToken);

    /**
     * Get user info from token
     * @return user info
     */
    JwtAuthentication getAuthInfo();

}
