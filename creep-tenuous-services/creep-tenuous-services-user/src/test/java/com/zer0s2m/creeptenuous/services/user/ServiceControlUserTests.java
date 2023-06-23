package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
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

@SpringBootTest(classes = {
        ServiceControlUserImpl.class,
        UserRepository.class
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

    @Test
    @Rollback
    public void getAllUsers_success() {
        RECORD_USER.setPassword("password");
        userRepository.save(RECORD_USER);

        Assertions.assertTrue(serviceControlUser.getAllUsers().size() >= 1);
    }

}
