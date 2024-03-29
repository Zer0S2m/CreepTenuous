package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.redis.repository.BlockUserDelayedRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.BlockUserRedisRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.redis.user.ServiceBlockUserRedisImpl;
import com.zer0s2m.creeptenuous.services.user.impl.ServiceControlUserImpl;
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

import java.time.LocalDateTime;

@SpringBootTest(classes = {
        ServiceControlUserImpl.class,
        UserRepository.class,
        ServiceBlockUserRedisImpl.class,
        BlockUserRedisRepository.class,
        BlockUserDelayedRedisRepository.class
})
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagService
@ContextConfiguration(classes = { ConfigServices.class })
@Rollback
public class ServiceControlUserTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceControlUserImpl serviceControlUser;

    User RECORD_USER = new User(
            "test_admin",
            null,
            "test_admin@test_admin.com",
            "test_admin"
    );

    User RECORD_DELETE_USER = new User(
            "test_login",
            null,
            "test_login@test_login.com",
            "test_login"
    );

    @Test
    @Rollback
    public void getAllUsers_success() {
        RECORD_USER.setPassword("password");
        userRepository.save(RECORD_USER);

        Assertions.assertTrue(serviceControlUser.getAllUsers().size() >= 1);
    }

    @Test
    @Rollback
    public void deleteUserByLogin_success() throws UserNotFoundException {
        RECORD_USER.setPassword("password");
        RECORD_DELETE_USER.setPassword("password");
        userRepository.save(RECORD_USER);
        userRepository.save(RECORD_DELETE_USER);

        serviceControlUser.deleteUser(RECORD_DELETE_USER.getLogin());

        Assertions.assertFalse(userRepository.existsUserByLogin("test_login"));
    }

    @Test
    @Rollback
    public void deleteUserByLogin_fail_notExistsUser() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceControlUser.deleteUser("user_not_found_by_login")
        );
    }

    @Test
    @Rollback
    public void blockUserByLogin_success() throws UserNotFoundException {
        RECORD_USER.setPassword("password");
        RECORD_DELETE_USER.setPassword("password");
        userRepository.save(RECORD_USER);
        userRepository.save(RECORD_DELETE_USER);

        serviceControlUser.blockUser(RECORD_DELETE_USER.getLogin());

        Assertions.assertFalse(userRepository.findByLogin(RECORD_DELETE_USER.getLogin()).isAccountNonLocked());
    }

    @Test
    @Rollback
    public void blockUserTemporarily_success_fromDateNow() {
        RECORD_USER.setPassword("password");
        userRepository.save(RECORD_USER);

        Assertions.assertDoesNotThrow(
                () -> serviceControlUser.blockUserTemporarily(
                        RECORD_USER.getLogin(), LocalDateTime.now(), LocalDateTime.now())
        );
    }

    @Test
    @Rollback
    public void blockUserTemporarily_success_notFromDate() {
        RECORD_USER.setPassword("password");
        userRepository.save(RECORD_USER);

        Assertions.assertDoesNotThrow(
                () -> serviceControlUser.blockUserTemporarily(
                        RECORD_USER.getLogin(), null, LocalDateTime.now())
        );
    }

    @Test
    @Rollback
    public void blockUserTemporarily_fail_notFoundUser() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceControlUser.blockUserTemporarily(
                        "not_found_user", null, LocalDateTime.now())
        );
    }

    @Test
    @Rollback
    public void blockUserByLogin_fail_notExistsUser() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceControlUser.blockUser("user_not_found_by_login")
        );
    }

    @Test
    @Rollback
    public void unblockUserByLogin_success() throws UserNotFoundException {
        RECORD_USER.setPassword("password");
        RECORD_DELETE_USER.setPassword("password");
        RECORD_DELETE_USER.setActivity(false);
        userRepository.save(RECORD_USER);
        userRepository.save(RECORD_DELETE_USER);

        serviceControlUser.unblockUser(RECORD_DELETE_USER.getLogin());

        Assertions.assertTrue(userRepository.findByLogin(RECORD_DELETE_USER.getLogin()).isAccountNonLocked());
    }

}
