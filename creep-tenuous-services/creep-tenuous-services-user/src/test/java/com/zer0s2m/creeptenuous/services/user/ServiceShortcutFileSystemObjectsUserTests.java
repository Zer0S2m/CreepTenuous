package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.data.DataControlFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.models.common.ShortcutFileSystemObject;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.repository.common.ShortcutFileSystemObjectRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.user.impl.ServiceShortcutFileSystemObjectsUserImpl;
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

import java.util.List;
import java.util.UUID;

@SpringBootTest(classes = {
        ServiceShortcutFileSystemObjectsUserImpl.class,
        UserRepository.class,
        ShortcutFileSystemObjectRepository.class
})
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagService
@ContextConfiguration(classes = { ConfigServices.class })
@Rollback
public class ServiceShortcutFileSystemObjectsUserTests {

    @Autowired
    private ServiceShortcutFileSystemObjectsUser serviceShortcutFileSystemObjectsUser;

    @Autowired
    private ShortcutFileSystemObjectRepository shortcutFileSystemObjectRepository;

    @Autowired
    private UserRepository userRepository;

    UUID systemName1 = UUID.randomUUID();

    UUID systemName2 = UUID.randomUUID();

    User RECORD_CREATE_USER = new User(
            UtilsAuthAction.LOGIN,
            "test_password",
            "test_admin@test_admin.com",
            "test_admin",
            UserRole.ROLE_USER
    );

    @Test
    public void create_success() {
        User user = userRepository.save(RECORD_CREATE_USER);

        Assertions.assertDoesNotThrow(
                () -> serviceShortcutFileSystemObjectsUser.create(
                        user.getLogin(), systemName1, systemName2));
    }

    @Test
    public void create_fail_userNotExists() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceShortcutFileSystemObjectsUser.create(
                        "user_not_found_in_db", systemName1, systemName2));
    }

    @Test
    public void delete_success() {
        User user = userRepository.save(RECORD_CREATE_USER);

        ShortcutFileSystemObject object = shortcutFileSystemObjectRepository.save(
                new ShortcutFileSystemObject(
                        user, systemName1, systemName2));

        Assertions.assertDoesNotThrow(
                () -> serviceShortcutFileSystemObjectsUser.delete(
                        user.getLogin(), systemName1, systemName2));
        Assertions.assertFalse(shortcutFileSystemObjectRepository.existsById(object.getId()));
    }

    @Test
    public void delete_fail_userNotExists() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceShortcutFileSystemObjectsUser.delete(
                        "user_not_found_in_db", systemName1, systemName2));
    }

    @Test
    public void show_success() {
        User user = userRepository.save(RECORD_CREATE_USER);
        shortcutFileSystemObjectRepository.save(
                new ShortcutFileSystemObject(
                        user, systemName1, systemName2));

        List<DataControlFileSystemObjectApi> objects = Assertions.assertDoesNotThrow(
                () -> serviceShortcutFileSystemObjectsUser.show(
                        user.getLogin(), systemName2)
        );
        Assertions.assertTrue(objects.size() >= 1);
    }

}
