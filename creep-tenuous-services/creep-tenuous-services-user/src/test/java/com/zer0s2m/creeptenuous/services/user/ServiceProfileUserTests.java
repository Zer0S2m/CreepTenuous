package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.user.impl.ServiceProfileUserImpl;
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

@SpringBootTest(classes = {
        ServiceProfileUserImpl.class,
        UserRepository.class
})
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagService
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceProfileUserTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceProfileUser serviceProfileUser;

    User RECORD_USER = new User(
            "test_login",
            "password",
            "test_login@test_login.com",
            "test_login"
    );

    @Test
    @Rollback
    public void getUserByLogin_success() {
        userRepository.save(RECORD_USER);

        final User user = serviceProfileUser.getUserByLogin(RECORD_USER.getLogin());

        Assertions.assertEquals(RECORD_USER.getLogin(), user.getLogin());
    }

}
