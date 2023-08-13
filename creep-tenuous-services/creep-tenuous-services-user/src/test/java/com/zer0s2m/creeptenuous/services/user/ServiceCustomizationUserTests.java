package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundUserColorDirectoryException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundUserColorException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.models.user.UserColor;
import com.zer0s2m.creeptenuous.models.user.UserColorDirectory;
import com.zer0s2m.creeptenuous.repository.user.UserColorDirectoryRepository;
import com.zer0s2m.creeptenuous.repository.user.UserColorRepository;
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
        UserColorRepository.class,
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
    private UserColorRepository userColorRepository;

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

        UserColor userColor = userColorRepository.save(new UserColor(
              user, "color"));

        Assertions.assertDoesNotThrow(
                () -> serviceCustomizationUser.setColorInDirectory(
                        systemName.toString(), user.getLogin(), userColor.getId())
        );
        Assertions.assertTrue(userColorDirectoryRepository.findByUserLoginAndDirectory(
                user.getLogin(), systemName
        ).isPresent());
    }

    @Test
    public void setColorInDirectory_edit_success() {
        User user = userRepository.save(RECORD_CREATE_USER);
        final UUID systemName = UUID.randomUUID();

        UserColor userColor = userColorRepository.save(new UserColor(
                user, "color"));

        userColorDirectoryRepository.save(
                new UserColorDirectory(user, userColor, systemName));

        Assertions.assertDoesNotThrow(
                () -> serviceCustomizationUser.setColorInDirectory(
                        systemName.toString(), user.getLogin(), userColor.getId()
                )
        );
    }

    @Test
    public void setColorInDirectory_fail_notFoundUser() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceCustomizationUser.setColorInDirectory(
                        "file", "user_login_not_found", 1L
                )
        );
    }

    @Test
    public void deleteColorInDirectory_success() {
        User user = userRepository.save(RECORD_CREATE_USER);
        final UUID systemName = UUID.randomUUID();

        UserColor userColor = userColorRepository.save(new UserColor(
                user, "color"));

        userColorDirectoryRepository.save(
                new UserColorDirectory(user, userColor, systemName));

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

    @Test
    public void createCustomColor_success() {
        User user = userRepository.save(RECORD_CREATE_USER);

        Assertions.assertDoesNotThrow(
                () -> serviceCustomizationUser.createColor(user.getLogin(), "color")
        );
    }

    @Test
    public void createCustomColor_fail_notFoundUser() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceCustomizationUser.createColor("user_login_not_found", "color")
        );
    }

    @Test
    public void editCustomColor_success() {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserColor userColor = userColorRepository.save(new UserColor(user, "color"));

        Assertions.assertDoesNotThrow(
                () -> serviceCustomizationUser.editColor(
                        user.getLogin(), userColor.getId(), "color2")
        );
        Assertions.assertEquals(
                "color2",
                userColorRepository.findByIdAndUserLogin(userColor.getId(), user.getLogin())
                        .get()
                        .getColor());
    }

    @Test
    public void editCustomColor_fail_notFoundUserColor() {
        Assertions.assertThrows(
                NotFoundUserColorException.class,
                () -> serviceCustomizationUser.editColor("user_login_not_found", 111L, "color")
        );
    }

    @Test
    public void deleteCustomColor_success() {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserColor userColor = userColorRepository.save(new UserColor(user, "color"));

        Assertions.assertDoesNotThrow(
                () -> serviceCustomizationUser.deleteColor(
                        user.getLogin(), userColor.getId())
        );
        Assertions.assertFalse(
                userColorRepository.existsById(userColor.getId())
        );
    }

    @Test
    public void CustomColor_fail_notFoundUserColor() {
        Assertions.assertThrows(
                NotFoundUserColorException.class,
                () -> serviceCustomizationUser.deleteColor("user_login_not_found", 111L)
        );
    }

}
