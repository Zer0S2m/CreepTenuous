package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundUserColorDirectoryException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.models.user.UserColorDirectory;
import com.zer0s2m.creeptenuous.repository.user.UserColorDirectoryRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.user.impl.ServiceCustomizationUserImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagService;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SpringBootTest(classes = {
        ServiceCustomizationUserImpl.class,
        UserColorDirectoryRepository.class,
        UserRepository.class
})
@Transactional
@Rollback
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagService
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceCustomizationUserTests {

    @Autowired
    private ServiceCustomizationUser serviceCustomizationUser;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserColorDirectoryRepository userColorDirectoryRepository;

    User RECORD_CREATE_USER = new User(
            UtilsAuthAction.LOGIN,
            "test_password",
            "test_admin@test_admin.com",
            "test_admin",
            UserRole.ROLE_USER
    );

    @Test
    public void setColorInDirectory_create_success() {
        User user = userRepository.save(RECORD_CREATE_USER);
        final UUID systemName = UUID.randomUUID();

        Assertions.assertDoesNotThrow(
                () -> serviceCustomizationUser.setColorInDirectory(
                        systemName.toString(), user.getLogin(), "color")
        );
        Assertions.assertTrue(userColorDirectoryRepository.findByUserLoginAndDirectory(
                user.getLogin(), systemName
        ).isPresent());
    }

    @Test
    public void setColorInDirectory_edit_success() {
        User user = userRepository.save(RECORD_CREATE_USER);
        final UUID systemName = UUID.randomUUID();

        userColorDirectoryRepository.save(
                new UserColorDirectory(user,"color", systemName));

        Assertions.assertDoesNotThrow(
                () -> serviceCustomizationUser.setColorInDirectory(
                        systemName.toString(), user.getLogin(), "color"
                )
        );
    }

    @Test
    public void setColorInDirectory_fail_notFoundUser() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceCustomizationUser.setColorInDirectory(
                        "file", "user_login_not_found", "color"
                )
        );
    }

    @Test
    public void deleteColorInDirectory_success() {
        User user = userRepository.save(RECORD_CREATE_USER);
        final UUID systemName = UUID.randomUUID();

        userColorDirectoryRepository.save(
                new UserColorDirectory(user,"color", systemName));

        Assertions.assertDoesNotThrow(
                () -> serviceCustomizationUser.deleteColorInDirectory(
                        systemName.toString(), user.getLogin()
                )
        );
        Assertions.assertTrue(userColorDirectoryRepository.findByUserLoginAndDirectory(
                user.getLogin(), systemName
        ).isEmpty());
    }

    @Test
    public void deleteColorInDirectory_fail_notFoundColorCategory() {
        Assertions.assertThrows(
                NotFoundUserColorDirectoryException.class,
                () -> serviceCustomizationUser.deleteColorInDirectory(
                        UUID.randomUUID().toString(), "user_login_not_found"
                )
        );
    }

}
