package CreepTenuous.providers.jwt.services.impl;

import CreepTenuous.models.User;
import CreepTenuous.providers.jwt.JwtProvider;
import CreepTenuous.providers.jwt.domain.JwtAuthentication;
import CreepTenuous.providers.jwt.exceptions.NoValidJwtRefreshTokenException;
import CreepTenuous.providers.jwt.http.JwtUserRequest;
import CreepTenuous.providers.jwt.http.JwtResponse;
import CreepTenuous.repositories.UserRepository;
import CreepTenuous.services.user.enums.UserException;
import CreepTenuous.services.user.exceptions.UserNotFoundException;
import CreepTenuous.services.user.exceptions.UserNotValidPasswordException;
import CreepTenuous.services.user.generatePassword.services.impl.GeneratePassword;
import CreepTenuous.providers.jwt.services.IJwtService;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("jwt-service")
@AllArgsConstructor
public class JwtService implements IJwtService {
    final private JwtProvider jwtProvider;

    final private UserRepository userRepository;

    final private GeneratePassword generatePassword;

    @Override
    public JwtResponse login(JwtUserRequest user) throws UserNotFoundException, UserNotValidPasswordException {
        User currentUser = userRepository.findByLogin(user.login());
        if (currentUser == null) {
            throw new UserNotFoundException(UserException.USER_NOT_IS_EXISTS.get());
        }
        if (!generatePassword.verify(user.password(), currentUser.getPassword())) {
            throw new UserNotValidPasswordException(UserException.USER_NOT_VALID_PASSWORD.get());
        }
        String accessToken = jwtProvider.generateAccessToken(user, currentUser.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user);

        return new JwtResponse(
                accessToken,
                refreshToken
        );
    }

    @Override
    public JwtResponse getAccessToken(@NonNull String refreshToken) throws UserNotFoundException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final User user = userRepository.findByLogin(login);
            if (user == null) {
                throw new UserNotFoundException(UserException.USER_NOT_IS_EXISTS.get());
            }

            return new JwtResponse(
                    jwtProvider.generateAccessToken(
                            new JwtUserRequest(user.getLogin(), null),
                            user.getRole()
                    ),
                    null
            );
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

            final JwtUserRequest jwtRequest = new JwtUserRequest(user.getLogin(), null);

            return new JwtResponse(
                    jwtProvider.generateAccessToken(jwtRequest, user.getRole()),
                    jwtProvider.generateRefreshToken(jwtRequest)
            );
        }
        throw new NoValidJwtRefreshTokenException("No valid refresh token");
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}
