package com.zer0s2m.creeptenuous.services.jwt;

import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.common.exceptions.AccountIsBlockedException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotValidPasswordException;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.redis.models.JwtRedis;
import com.zer0s2m.creeptenuous.redis.repository.JwtRedisRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.security.jwt.NoValidJwtRefreshTokenException;
import com.zer0s2m.creeptenuous.security.jwt.JwtResponse;
import com.zer0s2m.creeptenuous.security.jwt.JwtUserRequest;
import com.zer0s2m.creeptenuous.security.jwt.JwtProvider;
import com.zer0s2m.creeptenuous.security.services.GeneratePassword;
import com.zer0s2m.creeptenuous.services.redis.jwt.ServiceJwtRedisImpl;
import com.zer0s2m.creeptenuous.services.security.GeneratePasswordImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(classes = {
        UserRepository.class,
        JwtRedisRepository.class,
        JwtServiceImpl.class,
        JwtProvider.class,
        ServiceJwtRedisImpl.class
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagService
@ContextConfiguration(classes = { ConfigServices.class })
public class JwtServiceTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtRedisRepository jwtRedisDataRepository;

    @Autowired
    private JwtServiceImpl jwtService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private ServiceJwtRedisImpl redisService;

    private final GeneratePassword generatePassword = new GeneratePasswordImpl();

    User RECORD_USER = new User(
            "test_login",
            "test_password",
            "test_email@test.com",
            "test_name",
            UserRole.ROLE_USER
    );

    User RECORD_USER_INVALID = new User(
            "login",
            "password",
            "test_email_test_email@test.com",
            "test_name",
            UserRole.ROLE_USER
    );

    JwtUserRequest RECORD_USER_REQUEST = new JwtUserRequest("test_login", "test_password");
    JwtUserRequest INVALID_RECORD_USER_REQUEST = new JwtUserRequest("login", "password");
    JwtUserRequest INVALID_RECORD_USER_REQUEST_PASSWORD = new JwtUserRequest("test_login", "password");

    @Test
    @Rollback
    public void loginUser_success() throws UserNotFoundException, UserNotValidPasswordException,
            AccountIsBlockedException {
        RECORD_USER.setPassword(generatePassword.generation("test_password"));
        userRepository.save(RECORD_USER);

        JwtResponse response = jwtService.login(RECORD_USER_REQUEST);

        Assertions.assertTrue(jwtProvider.validateAccessToken(response.accessToken()));
        Assertions.assertTrue(jwtProvider.validateRefreshToken(response.refreshToken()));

        jwtRedisDataRepository.deleteById(RECORD_USER.getLogin());
    }

    @Test
    @Rollback
    public void loginUser_fail_accountLocked()  {
        RECORD_USER.setPassword(generatePassword.generation("test_password"));
        RECORD_USER.setActivity(false);
        userRepository.save(RECORD_USER);

        Assertions.assertThrows(
                AccountIsBlockedException.class,
                () -> jwtService.login(RECORD_USER_REQUEST)
        );

        jwtRedisDataRepository.deleteById(RECORD_USER.getLogin());
        RECORD_USER.setActivity(true);
    }

    @Test
    @Rollback
    public void generateAccessToken_success()
            throws UserNotFoundException, UserNotValidPasswordException, NoValidJwtRefreshTokenException,
            AccountIsBlockedException {
        RECORD_USER.setPassword(generatePassword.generation("test_password"));
        userRepository.save(RECORD_USER);

        JwtResponse response = jwtService.login(RECORD_USER_REQUEST);

        Assertions.assertTrue(jwtProvider.validateRefreshToken(response.refreshToken()));

        JwtResponse responseAccessToken = jwtService.getAccessToken(response.refreshToken());

        Assertions.assertTrue(jwtProvider.validateAccessToken(responseAccessToken.accessToken()));

        jwtRedisDataRepository.deleteById(RECORD_USER.getLogin());
    }

    @Test
    @Rollback
    public void generateAccessToken_fail_noValidRefreshToken() throws UserNotFoundException,
            NoValidJwtRefreshTokenException {
        final JwtResponse jwtResponse = jwtService.getAccessToken("refreshToken");
        Assertions.assertNull(jwtResponse.refreshToken());
        Assertions.assertNull(jwtResponse.accessToken());
    }

    @Test
    @Rollback
    public void generateAccessToken_fail_refreshTokenIsNull() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> jwtService.getAccessToken(null)
        );
    }

    @Test
    @Rollback
    public void generateAccessToken_fail_tokenMismatch()
            throws UserNotFoundException, UserNotValidPasswordException, AccountIsBlockedException {
        RECORD_USER.setPassword(generatePassword.generation("test_password"));
        userRepository.save(RECORD_USER);

        JwtResponse response = jwtService.login(RECORD_USER_REQUEST);

        Assertions.assertTrue(jwtProvider.validateRefreshToken(response.refreshToken()));

        final JwtRedis jwtRedis = jwtRedisDataRepository.findById(RECORD_USER.getLogin()).get();
        jwtRedis.setRefreshToken("newRefreshToken");
        jwtRedisDataRepository.save(jwtRedis);

        Assertions.assertThrows(
                NoValidJwtRefreshTokenException.class,
                () -> jwtService.getAccessToken(response.refreshToken())
        );

        jwtRedisDataRepository.deleteById(RECORD_USER.getLogin());
    }

    @Test
    @Rollback
    public void generateRefreshToken_success()
            throws UserNotFoundException, UserNotValidPasswordException, NoValidJwtRefreshTokenException,
            AccountIsBlockedException {
        RECORD_USER.setPassword(generatePassword.generation("test_password"));
        userRepository.save(RECORD_USER);

        JwtResponse response = jwtService.login(RECORD_USER_REQUEST);

        Assertions.assertTrue(jwtProvider.validateRefreshToken(response.refreshToken()));

        JwtResponse responseRefreshToken = jwtService.getRefreshToken(response.refreshToken());

        Assertions.assertTrue(jwtProvider.validateAccessToken(responseRefreshToken.accessToken()));
        Assertions.assertTrue(jwtProvider.validateRefreshToken(responseRefreshToken.refreshToken()));

        jwtRedisDataRepository.deleteById(RECORD_USER.getLogin());
    }

    @Test
    @Rollback
    public void generateRefreshToken_fail_tokenMismatch()
            throws UserNotFoundException, UserNotValidPasswordException, NoValidJwtRefreshTokenException,
            AccountIsBlockedException {
        RECORD_USER.setPassword(generatePassword.generation("test_password"));
        userRepository.save(RECORD_USER);

        JwtResponse response = jwtService.login(RECORD_USER_REQUEST);

        Assertions.assertTrue(jwtProvider.validateRefreshToken(response.refreshToken()));

        final JwtRedis jwtRedis = jwtRedisDataRepository.findById(RECORD_USER.getLogin()).get();
        jwtRedis.setRefreshToken("newRefreshToken");
        jwtRedisDataRepository.save(jwtRedis);

        Assertions.assertThrows(
                NoValidJwtRefreshTokenException.class,
                () -> jwtService.getRefreshToken(response.refreshToken())
        );

        jwtRedisDataRepository.deleteById(RECORD_USER.getLogin());
    }

    @Test
    @Rollback
    public void generateRefreshToken_fail_noValidRefreshToken() {
        Assertions.assertThrows(
                NoValidJwtRefreshTokenException.class,
                () -> jwtService.getRefreshToken("refreshToken")
        );
    }

    @Test
    @Rollback
    public void generateRefreshToken_fail_refreshTokenIsNull() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> jwtService.getRefreshToken(null)
        );
    }

    @Test
    public void loginUser_fail_notUserExists() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> jwtService.login(INVALID_RECORD_USER_REQUEST)
        );
    }

    @Test
    @Rollback
    public void loginUser_fail_invalidPassword() {
        RECORD_USER.setPassword(generatePassword.generation("test_password"));
        userRepository.save(RECORD_USER);

        Assertions.assertThrows(
                UserNotValidPasswordException.class,
                () -> jwtService.login(INVALID_RECORD_USER_REQUEST_PASSWORD)
        );

        jwtRedisDataRepository.deleteById(RECORD_USER.getLogin());
    }

    @Test
    @Rollback
    public void generateAccessToken_fail_userNotExists()
            throws UserNotFoundException, UserNotValidPasswordException, AccountIsBlockedException {
        RECORD_USER.setPassword(generatePassword.generation("test_password"));
        RECORD_USER_INVALID.setPassword(generatePassword.generation("password"));
        userRepository.save(RECORD_USER);
        userRepository.save(RECORD_USER_INVALID);

        JwtResponse responseInvalid = jwtService.login(INVALID_RECORD_USER_REQUEST);

        userRepository.delete(RECORD_USER_INVALID);

        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> jwtService.getAccessToken(responseInvalid.refreshToken())
        );

        jwtRedisDataRepository.deleteById(INVALID_RECORD_USER_REQUEST.login());
    }

    @Test
    @Rollback
    public void generateAccessToken_fail_invalidRefreshToken() {
        RECORD_USER.setPassword(generatePassword.generation("test_password"));
        userRepository.save(RECORD_USER);

        String refreshToken = jwtProvider.generateRefreshToken(
                new JwtUserRequest(
                        RECORD_USER.getLogin(),
                        "invalid_password"
                )
        );

        redisService.deleteTokensByLogin(RECORD_USER.getLogin());

        Assertions.assertThrows(
                NoValidJwtRefreshTokenException.class,
                () -> jwtService.getAccessToken(refreshToken)
        );

        jwtRedisDataRepository.deleteById(RECORD_USER.getLogin());
    }

    @Test
    @Rollback
    public void generateRefreshToken_fail_userNotExists()
            throws UserNotFoundException, UserNotValidPasswordException, AccountIsBlockedException {
        RECORD_USER.setPassword(generatePassword.generation("test_password"));
        RECORD_USER_INVALID.setPassword(generatePassword.generation("password"));
        userRepository.save(RECORD_USER);
        userRepository.save(RECORD_USER_INVALID);

        JwtResponse responseInvalid = jwtService.login(INVALID_RECORD_USER_REQUEST);

        userRepository.delete(RECORD_USER_INVALID);

        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> jwtService.getRefreshToken(responseInvalid.refreshToken())
        );

        jwtRedisDataRepository.deleteById(INVALID_RECORD_USER_REQUEST.login());
    }

    @Test
    @Rollback
    public void generateRefreshToken_fail_invalidRefreshToken() {
        RECORD_USER.setPassword(generatePassword.generation("test_password"));
        userRepository.save(RECORD_USER);

        String refreshToken = jwtProvider.generateRefreshToken(
                new JwtUserRequest(
                        RECORD_USER.getLogin(),
                        "invalid_password"
                )
        );

        redisService.deleteTokensByLogin(RECORD_USER.getLogin());

        Assertions.assertThrows(
                NoValidJwtRefreshTokenException.class,
                () -> jwtService.getRefreshToken(refreshToken)
        );

        jwtRedisDataRepository.deleteById(RECORD_USER.getLogin());
    }

    @Test
    @Rollback
    public void logout_success() throws UserNotFoundException, UserNotValidPasswordException,
            AccountIsBlockedException {
        RECORD_USER.setPassword(generatePassword.generation("test_password"));
        userRepository.save(RECORD_USER);

        JwtResponse response = jwtService.login(RECORD_USER_REQUEST);

        Assertions.assertDoesNotThrow(
                () -> jwtService.logout(response.accessToken())
        );
    }

    @Test
    @Rollback
    public void logout_fail_invalidToken() {
        Assertions.assertDoesNotThrow(
                () -> jwtService.logout("accessToken")
        );
    }

}
