package com.zer0s2m.creeptenuous.repository.user;

import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.repository.ConfigRepository;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagRepository
@ContextConfiguration(classes = { ConfigRepository.class })
public class UserRepositoryDBTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createUser_success() {
        User newUser = getFataDorCreateUser();
        userRepository.save(newUser);
        User user = userRepository.findByLogin(newUser.getLogin());

        Assertions.assertNotNull(user);
        Assertions.assertEquals("test_login", user.getLogin());
        Assertions.assertEquals("test_email@test.com", user.getEmail());
        Assertions.assertEquals("test_name", user.getName());
    }

    @Test
    public void getUserByLoginAndExistsUser_success() {
        User newUser = getFataDorCreateUser();
        User createdUser = userRepository.save(newUser);

        Assertions.assertNotNull(createdUser);
        Assertions.assertTrue(userRepository.existsUserByLogin(createdUser.getLogin()));
    }

    @Test
    public void getUserByEmailAndExistsUser_success() {
        User newUser = getFataDorCreateUser();
        User createdUser = userRepository.save(newUser);

        Assertions.assertNotNull(createdUser);
        Assertions.assertTrue(userRepository.existsUserByEmail(createdUser.getEmail()));
    }

    @Test
    public void getUserByLoginAndNotExistsUser_success() {
        User newUser = getFataDorCreateUser();

        Assertions.assertFalse(userRepository.existsUserByLogin(newUser.getLogin()));
    }

    @Test
    public void getUserByEmailAndNotExistsUser_success() {
        User newUser = getFataDorCreateUser();

        Assertions.assertFalse(userRepository.existsUserByEmail(newUser.getEmail()));
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
