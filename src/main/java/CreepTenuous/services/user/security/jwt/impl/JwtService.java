package CreepTenuous.services.user.security.jwt.impl;

import CreepTenuous.models.User;
import CreepTenuous.providers.jwt.JwtProvider;
import CreepTenuous.providers.jwt.http.JwtUserRequest;
import CreepTenuous.providers.jwt.http.JwtResponse;
import CreepTenuous.repositories.UserRepository;
import CreepTenuous.services.user.enums.UserException;
import CreepTenuous.services.user.exceptions.UserNotFoundException;
import CreepTenuous.services.user.exceptions.UserNotValidPasswordException;
import CreepTenuous.services.user.generatePassword.services.impl.GeneratePassword;
import CreepTenuous.services.user.security.jwt.IJwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("jwt-service")
public class JwtService implements IJwtService {
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeneratePassword generatePassword;

    @Override
    public JwtResponse login(JwtUserRequest user) throws UserNotFoundException, UserNotValidPasswordException {
        User currentUser = userRepository.findUserByLogin(user.getLogin());
        if (currentUser == null) {
            throw new UserNotFoundException(UserException.USER_NOT_IS_EXISTS.get());
        }
        if (!generatePassword.verify(user.getPassword(), currentUser.getPassword())) {
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
    public void access() {

    }

    @Override
    public void refresh() {

    }
}
