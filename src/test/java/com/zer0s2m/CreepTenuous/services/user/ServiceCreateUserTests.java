package com.zer0s2m.CreepTenuous.services.user;

import com.zer0s2m.CreepTenuous.helpers.TestTagService;
import com.zer0s2m.CreepTenuous.models.User;
import com.zer0s2m.CreepTenuous.services.user.create.services.impl.ServiceCreateUser;
import com.zer0s2m.CreepTenuous.services.user.enums.UserRole;
import com.zer0s2m.CreepTenuous.services.user.exceptions.UserAlreadyExistException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagService
public class ServiceCreateUserTests {
    Logger logger = LogManager.getLogger(ServiceCreateUserTests.class);

    @Autowired
    private ServiceCreateUser service;

    @Test
    @Rollback
    public void createUser_success() throws UserAlreadyExistException {
        User newUser = getFataDorCreateUser();
        Long userId = service.create(newUser);

        logger.info("Create new user in db: " + newUser);

        Assertions.assertNotNull(userId);
    }

    @Test
    @Rollback
    public void createUser_fail_userAlreadyExistsLogin() throws UserAlreadyExistException {
        User newUser1 = getFataDorCreateUser();
        Long userId1 = service.create(newUser1);

        logger.info("Create new user in db: " + newUser1);

        Assertions.assertNotNull(userId1);

        Assertions.assertThrows(
                UserAlreadyExistException.class,
                () -> service.create(getFataDorCreateUser())
        );
    }

    @Test
    @Rollback
    public void createUser_fail_userAlreadyExistsEmail() throws UserAlreadyExistException {
        User newUser1 = getFataDorCreateUser();
        Long userId1 = service.create(newUser1);

        logger.info("Create new user in db: " + newUser1);

        Assertions.assertNotNull(userId1);

        User newUser2 = getFataDorCreateUser();
        newUser2.setLogin("test_login_2");
        Assertions.assertThrows(
                UserAlreadyExistException.class,
                () -> service.create(newUser2)
        );
    }

    protected User getFataDorCreateUser() {
        return new User(
                "test_login",
                "test_password",
                "test_email@test.com",
                "test_name",
                UserRole.ROLE_USER
        );
    }
}

