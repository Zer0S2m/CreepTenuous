package com.zer0s2m.CreepTenuous.services.jwt;

import com.zer0s2m.CreepTenuous.models.User;
import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.jwt.exceptions.NoValidJwtRefreshTokenException;
import com.zer0s2m.CreepTenuous.providers.jwt.http.JwtResponse;
import com.zer0s2m.CreepTenuous.providers.jwt.http.JwtUserRequest;
import com.zer0s2m.CreepTenuous.providers.jwt.services.impl.JwtService;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.JwtRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.impl.ServiceJwtRedis;
import com.zer0s2m.CreepTenuous.repositories.UserRepository;
import com.zer0s2m.CreepTenuous.services.user.enums.UserRole;
import com.zer0s2m.CreepTenuous.services.user.exceptions.UserNotFoundException;
import com.zer0s2m.CreepTenuous.services.user.exceptions.UserNotValidPasswordException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class JwtServiceTests {
    Logger logger = LogManager.getLogger(JwtServiceTests.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtRedisRepository jwtRedisDataRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private ServiceJwtRedis redisService;

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
    public void loginUser_success() throws UserNotFoundException, UserNotValidPasswordException {
        userRepository.save(RECORD_USER);

        logger.info("Create user for tests: " + RECORD_USER);

        JwtResponse response = jwtService.login(RECORD_USER_REQUEST);

        Assertions.assertTrue(jwtProvider.validateAccessToken(response.accessToken()));
        Assertions.assertTrue(jwtProvider.validateRefreshToken(response.refreshToken()));

        jwtRedisDataRepository.deleteById(RECORD_USER.getLogin());
    }

    @Test
    @Rollback
    public void generateAccessToken_success()
            throws UserNotFoundException, UserNotValidPasswordException, NoValidJwtRefreshTokenException
    {
        userRepository.save(RECORD_USER);

        logger.info("Create user for tests: " + RECORD_USER);

        JwtResponse response = jwtService.login(RECORD_USER_REQUEST);

        Assertions.assertTrue(jwtProvider.validateRefreshToken(response.refreshToken()));

        JwtResponse responseAccessToken = jwtService.getAccessToken(response.refreshToken());

        Assertions.assertTrue(jwtProvider.validateAccessToken(responseAccessToken.accessToken()));

        jwtRedisDataRepository.deleteById(RECORD_USER.getLogin());
    }

    @Test
    @Rollback
    public void generateRefreshToken_success()
            throws UserNotFoundException, UserNotValidPasswordException, NoValidJwtRefreshTokenException
    {
        userRepository.save(RECORD_USER);

        logger.info("Create user for tests: " + RECORD_USER);

        JwtResponse response = jwtService.login(RECORD_USER_REQUEST);

        Assertions.assertTrue(jwtProvider.validateRefreshToken(response.refreshToken()));

        JwtResponse responseRefreshToken = jwtService.getRefreshToken(response.refreshToken());

        Assertions.assertTrue(jwtProvider.validateAccessToken(responseRefreshToken.accessToken()));
        Assertions.assertTrue(jwtProvider.validateRefreshToken(responseRefreshToken.refreshToken()));

        jwtRedisDataRepository.deleteById(RECORD_USER.getLogin());
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
        userRepository.save(RECORD_USER);

        logger.info("Create user for tests: " + RECORD_USER);

        Assertions.assertThrows(
                UserNotValidPasswordException.class,
                () -> jwtService.login(INVALID_RECORD_USER_REQUEST_PASSWORD)
        );

        jwtRedisDataRepository.deleteById(RECORD_USER.getLogin());
    }

    @Test
    @Rollback
    public void generateAccessToken_fail_userNotExists()
            throws UserNotFoundException, UserNotValidPasswordException
    {
        userRepository.save(RECORD_USER);
        userRepository.save(RECORD_USER_INVALID);

        logger.info("Create user for tests: " + RECORD_USER);
        logger.info("Create user for tests: " + RECORD_USER_INVALID);

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
        userRepository.save(RECORD_USER);

        logger.info("Create user for tests: " + RECORD_USER);

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
            throws UserNotFoundException, UserNotValidPasswordException
    {
        userRepository.save(RECORD_USER);
        userRepository.save(RECORD_USER_INVALID);

        logger.info("Create user for tests: " + RECORD_USER);
        logger.info("Create user for tests: " + RECORD_USER_INVALID);

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
        userRepository.save(RECORD_USER);

        logger.info("Create user for tests: " + RECORD_USER);

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
}
