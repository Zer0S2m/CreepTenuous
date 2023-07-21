package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.containers.ContainerCategoryFileSystemObject;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataUserCategory;
import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundCategoryFileSystemObjectException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundUserCategoryException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.models.user.CategoryFileSystemObject;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.models.user.UserCategory;
import com.zer0s2m.creeptenuous.repository.user.CategoryFileSystemObjectRepository;
import com.zer0s2m.creeptenuous.repository.user.UserCategoryRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.user.impl.ServiceCategoryUserImpl;
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
        ServiceCategoryUserImpl.class,
        UserRepository.class,
        UserCategoryRepository.class,
        CategoryFileSystemObjectRepository.class
})
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagService
@ContextConfiguration(classes = { ConfigServices.class })
@Rollback
public class ServiceCategoryUserTests {

    @Autowired
    private ServiceCategoryUser serviceCategoryUser;

    @Autowired
    private UserCategoryRepository userCategoryRepository;

    @Autowired
    private CategoryFileSystemObjectRepository categoryFileSystemObjectRepository;

    @Autowired
    private UserRepository userRepository;

    User RECORD_CREATE_USER = new User(
            UtilsAuthAction.LOGIN,
            "test_password",
            "test_admin@test_admin.com",
            "test_admin",
            UserRole.ROLE_USER
    );

    @Test
    public void getAll_success() {
        User user = userRepository.save(RECORD_CREATE_USER);
        userCategoryRepository.save(new UserCategory("Title", user));

        List<ContainerDataUserCategory> data = Assertions.assertDoesNotThrow(
                () -> serviceCategoryUser.getAll(user.getLogin())
        );
        Assertions.assertTrue(data.size() >= 1);
    }

    @Test
    public void create_success() {
        User user = userRepository.save(RECORD_CREATE_USER);

        ContainerDataUserCategory data = Assertions.assertDoesNotThrow(
                () -> serviceCategoryUser.create("Title", user.getLogin())
        );
        Assertions.assertEquals(data.title(), "Title");
    }

    @Test
    public void create_fail_notFoundUser() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceCategoryUser.create("title", "not_found_login_user")
        );
    }

    @Test
    public void edit_success() {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserCategory userCategory = userCategoryRepository.save(new UserCategory(
                "Title", user));

        Assertions.assertDoesNotThrow(
                () -> serviceCategoryUser.edit(userCategory.getId(), "New title", user.getLogin())
        );
    }

    @Test
    public void edit_fail_notFoundCategory() {
        Assertions.assertThrows(
                NotFoundUserCategoryException.class,
                () -> serviceCategoryUser.edit(1333L, "New title", "not_found_login_user")
        );
    }

    @Test
    public void delete_success() {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserCategory userCategory = userCategoryRepository.save(new UserCategory(
                "Title", user));

        Assertions.assertDoesNotThrow(
                () -> serviceCategoryUser.delete(userCategory.getId(), user.getLogin())
        );
        Assertions.assertFalse(userCategoryRepository.existsById(userCategory.getId()));
    }

    @Test
    public void delete_fail_notFoundCategory() {
        Assertions.assertThrows(
                NotFoundUserCategoryException.class,
                () -> serviceCategoryUser.delete(12333L, "not_found_login_user")
        );
    }

    @Test
    @Rollback
    public void setFileSystemObjectInCategory_success() {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserCategory userCategory = userCategoryRepository.save(new UserCategory(
                "Title", user));

        Assertions.assertDoesNotThrow(
                () -> serviceCategoryUser.setFileSystemObjectInCategory(
                        userCategory.getId(), UUID.randomUUID().toString(), user.getLogin())
        );
    }

    @Test
    public void setFileSystemObjectInCategory_fail_notFoundUser() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceCategoryUser.setFileSystemObjectInCategory(
                        1L, "systemName", "not_found_login_user")
        );
    }

    @Test
    public void setFileSystemObjectInCategory_fail_notFoundCategory() {
        User user = userRepository.save(RECORD_CREATE_USER);
        Assertions.assertThrows(
                NotFoundUserCategoryException.class,
                () -> serviceCategoryUser.setFileSystemObjectInCategory(
                        123322L, "systemName", user.getLogin())
        );
    }

    @Test
    @Rollback
    public void unsetFileSystemObjectInCategory_success() {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserCategory userCategory = userCategoryRepository.save(new UserCategory(
                "Title", user));
        final UUID systemName = UUID.randomUUID();
        CategoryFileSystemObject categoryFileSystemObject = categoryFileSystemObjectRepository.save(
                new CategoryFileSystemObject(user, userCategory, systemName));

        Assertions.assertDoesNotThrow(
                () -> serviceCategoryUser.unsetFileSystemObjectInCategory(
                        userCategory.getId(), categoryFileSystemObject.getFileSystemObject().toString(),
                        user.getLogin())
        );
        Assertions.assertFalse(categoryFileSystemObjectRepository.existsById(categoryFileSystemObject.getId()));
    }

    @Test
    public void unsetFileSystemObjectInCategory_fail_notFoundCategory() {
        Assertions.assertThrows(
                NotFoundUserCategoryException.class,
                () -> serviceCategoryUser.unsetFileSystemObjectInCategory(
                        123322L, "systemName", "login")
        );
    }

    @Test
    @Rollback
    public void unsetFileSystemObjectInCategory_notFoundCategoryFileSystemObject() {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserCategory userCategory = userCategoryRepository.save(new UserCategory(
                "Title", user));

        Assertions.assertThrows(
                NotFoundCategoryFileSystemObjectException.class,
                () -> serviceCategoryUser.unsetFileSystemObjectInCategory(
                        userCategory.getId(), UUID.randomUUID().toString(), user.getLogin())
        );
    }

    @Test
    @Rollback
    public void getFileSystemObjectInCategoryByCategoryId_success() {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserCategory userCategory = userCategoryRepository.save(new UserCategory(
                "Title", user));
        categoryFileSystemObjectRepository.save(
                new CategoryFileSystemObject(user, userCategory, UUID.randomUUID()));

        List<ContainerCategoryFileSystemObject> data = Assertions.assertDoesNotThrow(
                () -> serviceCategoryUser.getFileSystemObjectInCategoryByCategoryId(
                        userCategory.getId(), user.getLogin())
        );
        Assertions.assertTrue(data.size() >= 1);
    }

    @Test
    public void getFileSystemObjectInCategoryByCategoryId_fail_notFoundCategory() {
        Assertions.assertThrows(
                NotFoundUserCategoryException.class,
                () -> serviceCategoryUser.getFileSystemObjectInCategoryByCategoryId(
                        123L, "user_not_found")
        );
    }

}
