package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.components.UploadAvatar;
import com.zer0s2m.creeptenuous.common.exceptions.UploadAvatarForUserException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.models.user.UserFileObjectsExclusion;
import com.zer0s2m.creeptenuous.models.user.UserSettings;
import com.zer0s2m.creeptenuous.repository.user.UserFileObjectsExclusionRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.repository.user.UserSettingsRepository;
import com.zer0s2m.creeptenuous.services.user.impl.ServiceProfileUserImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest(classes = {
        ServiceProfileUserImpl.class,
        UserRepository.class,
        UserSettingsRepository.class,
        UserFileObjectsExclusionRepository.class,
        UploadAvatar.class
})
@Transactional
@Rollback
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagService
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceProfileUserTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private ServiceProfileUser serviceProfileUser;

    @Autowired
    private UserFileObjectsExclusionRepository userFileObjectsExclusionRepository;

    @Autowired
    private UploadAvatar uploadAvatar;

    final private String nameTestFile1 = "test_image_1.jpeg";

    User RECORD_USER = new User(
            "test_login",
            "password",
            "test_login@test_login.com",
            "test_login"
    );

    User RECORD_USER_2 = new User(
            "test_login_2",
            "password_2",
            "test_login_2@test_login_2.com",
            "test_login_2"
    );

    @Test
    public void getUserByLogin_success() {
        userRepository.save(RECORD_USER);

        final User user = serviceProfileUser.getUserByLogin(RECORD_USER.getLogin());

        Assertions.assertEquals(RECORD_USER.getLogin(), user.getLogin());
    }

    @Test
    public void setIsDeletingFileObjectSettings_success_settingsEmpty() {
        userRepository.save(RECORD_USER);
        Assertions.assertDoesNotThrow(
                () -> serviceProfileUser.setIsDeletingFileObjectSettings(RECORD_USER.getLogin(), false)
        );

        Optional<UserSettings> userSettings = userSettingsRepository.findByUser_Login(RECORD_USER.getLogin());

        Assertions.assertTrue(userSettings.isPresent());
        Assertions.assertFalse(userSettings.get().getIsDeletingFileObjects());
    }

    @Test
    public void setIsDeletingFileObjectSettings_success_settingsPresent() {
        User userSaved = userRepository.save(RECORD_USER);
        userSettingsRepository.save(new UserSettings(
                userSaved, false));
        Assertions.assertDoesNotThrow(
                () -> serviceProfileUser.setIsDeletingFileObjectSettings(RECORD_USER.getLogin(), true)
        );

        Optional<UserSettings> userSettings = userSettingsRepository.findByUser_Login(RECORD_USER.getLogin());

        Assertions.assertTrue(userSettings.isPresent());
        Assertions.assertTrue(userSettings.get().getIsDeletingFileObjects());
    }

    @Test
    public void setIsDeletingFileObjectSettings_fail_notFoundUsers() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceProfileUser.setIsDeletingFileObjectSettings("user_not_found", false));
    }

    @Test
    public void setTransferredUserSettings_success_settingsEmpty() {
        userRepository.save(RECORD_USER);
        User user2 = userRepository.save(RECORD_USER_2);

        Assertions.assertDoesNotThrow(
                () -> serviceProfileUser.setTransferredUserSettings(RECORD_USER.getLogin(), user2.getId()));

        Optional<UserSettings> userSettings = userSettingsRepository.findByUser_Login(RECORD_USER.getLogin());

        Assertions.assertTrue(userSettings.isPresent());
        Assertions.assertEquals(userSettings.get().getTransferredUser().getId(), user2.getId());
    }

    @Test
    public void setTransferredUserSettings_success_settingsPresent() {
        User userSaved = userRepository.save(RECORD_USER);
        User user2 = userRepository.save(RECORD_USER_2);
        userSettingsRepository.save(new UserSettings(userSaved, user2));

        Assertions.assertDoesNotThrow(
                () -> serviceProfileUser.setTransferredUserSettings(RECORD_USER.getLogin(), user2.getId()));

        Optional<UserSettings> userSettings = userSettingsRepository.findByUser_Login(RECORD_USER.getLogin());

        Assertions.assertTrue(userSettings.isPresent());
        Assertions.assertEquals(userSettings.get().getTransferredUser().getId(), user2.getId());
    }

    @Test
    public void setTransferredUserSettings_success_deleteTransferUser() {
        userRepository.save(RECORD_USER);

        Assertions.assertDoesNotThrow(
                () -> serviceProfileUser.setTransferredUserSettings(RECORD_USER.getLogin(), null));

        Optional<UserSettings> userSettings = userSettingsRepository.findByUser_Login(RECORD_USER.getLogin());

        Assertions.assertTrue(userSettings.isPresent());
        Assertions.assertNull(userSettings.get().getTransferredUser());
    }

    @Test
    public void setTransferredUserSettings_fail_userNotFound() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceProfileUser.setTransferredUserSettings("user_not_found", 1L));
    }

    @Test
    public void setTransferredUserSettings_fail_userTransferNotFound() {
        userRepository.save(RECORD_USER);

        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceProfileUser.setTransferredUserSettings(RECORD_USER.getLogin(), 123123L));
    }

    @Test
    public void setFileObjectsExclusion_success() {
        userRepository.save(RECORD_USER);

        Assertions.assertDoesNotThrow(
                () -> serviceProfileUser.setFileObjectsExclusion(
                        List.of(UUID.randomUUID()), RECORD_USER.getLogin()));
    }

    @Test
    public void setFileObjectsExclusion_fail_userNotFound() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceProfileUser.setFileObjectsExclusion(
                        List.of(UUID.randomUUID()), "login_user_not_found"));
    }

    @Test
    public void deleteFileObjectsExclusion_success() {
        User user = userRepository.save(RECORD_USER);
        UUID systemName = UUID.randomUUID();

        UserFileObjectsExclusion userFileObjectsExclusion = userFileObjectsExclusionRepository
                .save(new UserFileObjectsExclusion(systemName, user));

        Assertions.assertDoesNotThrow(
                () -> serviceProfileUser.deleteFileObjectsExclusion(
                        List.of(systemName), RECORD_USER.getLogin()));
        Assertions.assertFalse(userFileObjectsExclusionRepository.existsById(
                userFileObjectsExclusion.getId()));
    }

    @Test
    public void deleteFileObjectsExclusion_fail_userNotFound() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceProfileUser.deleteFileObjectsExclusion(
                        List.of(UUID.randomUUID()), "login_user_not_found"));
    }

    @Test
    public void uploadFile_success_avatarNotIsNull() throws IOException {
        Path pathAvatar1 = Path.of(uploadAvatar.getUploadAvatarDir(), nameTestFile1);

        try (InputStream inputAvatar = this.getClass().getResourceAsStream("/" + nameTestFile1)) {
            RECORD_USER.setAvatar(pathAvatar1.toString());
            userRepository.save(RECORD_USER);

            assert inputAvatar != null;
            Files.copy(inputAvatar, pathAvatar1);
        }

        String titleAvatar = Assertions.assertDoesNotThrow(
                () -> serviceProfileUser.uploadAvatar(
                        new MockMultipartFile(
                                "file",
                                "file.jpeg",
                                MediaType.IMAGE_JPEG_VALUE,
                                this.getClass().getResourceAsStream("/" + nameTestFile1)
                        ), RECORD_USER.getLogin())
        );
        Path pathAvatar2 = Path.of(uploadAvatar.getUploadAvatarDir(), titleAvatar);

        Assertions.assertTrue(Files.exists(pathAvatar2));
        Assertions.assertFalse(Files.exists(pathAvatar1));

        Files.deleteIfExists(pathAvatar2);
    }

    @Test
    public void uploadFile_success_avatarIsNull() throws IOException {
        userRepository.save(RECORD_USER);

        String titleAvatar = Assertions.assertDoesNotThrow(
                () -> serviceProfileUser.uploadAvatar(
                        new MockMultipartFile(
                                "file",
                                "file.jpeg",
                                MediaType.IMAGE_JPEG_VALUE,
                                this.getClass().getResourceAsStream("/" + nameTestFile1)
                        ), RECORD_USER.getLogin())
        );
        Path pathAvatar = Path.of(uploadAvatar.getUploadAvatarDir(), titleAvatar);

        Assertions.assertTrue(Files.exists(pathAvatar));

        Files.deleteIfExists(pathAvatar);
    }

    @Test
    public void uploadFile_success_avatarInvalidPath() {
        userRepository.save(RECORD_USER);

        Assertions.assertThrows(
                UploadAvatarForUserException.class,
                () -> serviceProfileUser.uploadAvatar(
                        new MockMultipartFile(
                                "file",
                                "file_1..1.jpeg",
                                MediaType.IMAGE_JPEG_VALUE,
                                this.getClass().getResourceAsStream("/" + nameTestFile1)
                        ), RECORD_USER.getLogin())
        );
    }

    @Test
    public void uploadFile_fail_notFoundUser() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceProfileUser.uploadAvatar(
                        new MockMultipartFile(
                                "file",
                                "file.jpeg",
                                MediaType.IMAGE_JPEG_VALUE,
                                this.getClass().getResourceAsStream("/" + nameTestFile1)
                        ), "login_user_not_found"));
    }

    @Test
    public void deleteAvatar_success_avatarIsNull() {
        userRepository.save(RECORD_USER);

        Assertions.assertDoesNotThrow(
                () -> serviceProfileUser.deleteAvatar(RECORD_USER.getLogin()));

        User user = userRepository.findByLogin(RECORD_USER.getLogin());

        Assertions.assertNull(user.getAvatar());
    }

    @Test
    public void deleteAvatar_success_avatarNotIsNull() throws IOException {
        Path pathAvatar = Path.of(uploadAvatar.getUploadAvatarDir(), nameTestFile1);

        try (InputStream inputAvatar = this.getClass().getResourceAsStream("/" + nameTestFile1)) {
            RECORD_USER.setAvatar(pathAvatar.toString());
            userRepository.save(RECORD_USER);

            assert inputAvatar != null;
            Files.copy(inputAvatar, pathAvatar);
        }

        Assertions.assertDoesNotThrow(
                () -> serviceProfileUser.deleteAvatar(RECORD_USER.getLogin()));

        Assertions.assertFalse(Files.exists(pathAvatar));
    }

    @Test
    public void deleteAvatar_fail_userNotFound() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceProfileUser.deleteAvatar("login_user_not_found"));
    }

}
