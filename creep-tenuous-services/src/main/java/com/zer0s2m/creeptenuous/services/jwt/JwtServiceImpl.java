package com.zer0s2m.creeptenuous.services.jwt;

import com.zer0s2m.creeptenuous.common.enums.UserException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotValidPasswordException;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.redis.jwt.ServiceJwtRedisImpl;
import com.zer0s2m.creeptenuous.services.security.GeneratePasswordImpl;
import com.zer0s2m.creeptenuous.redis.models.JwtRedis;
import com.zer0s2m.creeptenuous.security.jwt.exceptions.NoValidJwtRefreshTokenException;
import com.zer0s2m.creeptenuous.security.jwt.http.JwtResponse;
import com.zer0s2m.creeptenuous.security.jwt.http.JwtUserRequest;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.services.JwtService;
import com.zer0s2m.creeptenuous.security.jwt.domain.JwtAuthentication;
import com.zer0s2m.creeptenuous.security.jwt.domain.JwtRedisData;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service("jwt-service")
@AllArgsConstructor
public class JwtServiceImpl implements JwtService {
    final private JwtProvider jwtProvider;

    final private UserRepository userRepository;

    final private GeneratePasswordImpl generatePassword;

    final private ServiceJwtRedisImpl redisService;

    @Override
    public JwtResponse login(JwtUserRequest user) throws UserNotFoundException, UserNotValidPasswordException {
        User currentUser = userRepository.findByLogin(user.login());
        if (currentUser == null) {
            throw new UserNotFoundException(UserException.USER_NOT_IS_EXISTS.get());
        }
        if (!generatePassword.verify(user.password(), currentUser.getPassword())) {
            throw new UserNotValidPasswordException(UserException.USER_NOT_VALID_PASSWORD.get());
        }

        redisService.deleteTokensByLogin(currentUser.getLogin());

        final String accessToken = jwtProvider.generateAccessToken(user, currentUser.getRole());
        final String refreshToken = jwtProvider.generateRefreshToken(user);

        redisService.save(new JwtRedisData(
                currentUser.getLogin(),
                accessToken,
                refreshToken
        ));

        return new JwtResponse(
                accessToken,
                refreshToken
        );
    }

    @Override
    public JwtResponse getAccessToken(
            @NonNull String refreshToken
    ) throws UserNotFoundException, NoValidJwtRefreshTokenException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final User user = userRepository.findByLogin(login);
            if (user == null) {
                throw new UserNotFoundException(UserException.USER_NOT_IS_EXISTS.get());
            }

            Optional<JwtRedis> jwtRedis = redisService.getByLogin(login);
            if (jwtRedis.isEmpty()) {
                throw new NoValidJwtRefreshTokenException("No valid refresh token");
            }
            if (!Objects.equals(jwtRedis.get().getRefreshToken(), refreshToken)) {
                throw new NoValidJwtRefreshTokenException("No valid refresh token");
            }

            final String newAccessToken = jwtProvider.generateAccessToken(
                    new JwtUserRequest(user.getLogin(), null),
                    user.getRole()
            );

            redisService.updateAccessToken(new JwtRedisData(
                    login, newAccessToken,null
            ));

            return new JwtResponse(newAccessToken, null);
        }
        return new JwtResponse(null, null);
    }

    @Override
    public JwtResponse getRefreshToken(
            @NonNull String refreshToken
    ) throws NoValidJwtRefreshTokenException, UserNotFoundException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final User user = userRepository.findByLogin(login);

            if (user == null) {
                throw new UserNotFoundException(UserException.USER_NOT_IS_EXISTS.get());
            }
            Optional<JwtRedis> jwtRedis = redisService.getByLogin(login);
            if (jwtRedis.isEmpty()) {
                throw new NoValidJwtRefreshTokenException("No valid refresh token");
            }
            JwtRedis readyJwtRedis = jwtRedis.get();
            if (!Objects.equals(readyJwtRedis.getRefreshToken(), refreshToken)) {
                throw new NoValidJwtRefreshTokenException("No valid refresh token");
            }

            final JwtUserRequest jwtRequest = new JwtUserRequest(login, null);
            final String newRefreshToken = jwtProvider.generateRefreshToken(jwtRequest);
            final String newAccessToken = jwtProvider.generateAccessToken(jwtRequest, user.getRole());

            JwtRedisData newJwtRedisData = new JwtRedisData(login, newAccessToken, newRefreshToken);
            if (Objects.equals(readyJwtRedis.getRefreshToken(), refreshToken)) {
                redisService.updateTokens(newJwtRedisData);
            } else {
                redisService.save(newJwtRedisData);
            }

            return new JwtResponse(newAccessToken, newRefreshToken);
        }
        throw new NoValidJwtRefreshTokenException("No valid refresh token");
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}
