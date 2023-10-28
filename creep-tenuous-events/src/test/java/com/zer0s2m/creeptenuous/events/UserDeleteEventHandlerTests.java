package com.zer0s2m.creeptenuous.events;

import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.enums.ManagerRights;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.models.user.UserFileObjectsExclusion;
import com.zer0s2m.creeptenuous.models.user.UserSettings;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.models.JwtRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.JwtRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.RightUserFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.resources.ServiceRedisManagerResources;
import com.zer0s2m.creeptenuous.repository.user.UserFileObjectsExclusionRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.repository.user.UserSettingsRepository;
import com.zer0s2m.creeptenuous.services.redis.resources.ServiceRedisManagerResourcesImpl;
import com.zer0s2m.creeptenuous.services.redis.security.ServiceControlUserRightsImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest(classes = {
        UserDeleteEventHandler.class,
        UserEventPublisher.class,
        ServiceControlUserRightsImpl.class,
        ServiceRedisManagerResourcesImpl.class,
        UserSettingsRepository.class,
        UserFileObjectsExclusionRepository.class,
        DirectoryRedisRepository.class,
        FileRedisRepository.class,
        RightUserFileSystemObjectRedisRepository.class,
        ServiceRedisManagerResources.class,
        JwtRedisRepository.class,
        UserRepository.class
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagEvent
@Transactional
@Rollback
@ContextConfiguration(classes = { ConfigEvents.class })
public class UserDeleteEventHandlerTests {

    @Autowired
    private UserDeleteEventHandler userDeleteEventHandler;

    @Autowired
    private UserEventPublisher userEventPublisher;

    private final RootPath rootPath = new RootPath();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @Autowired
    private RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    @Autowired
    private UserFileObjectsExclusionRepository userFileObjectsExclusionRepository;

    @Autowired
    private JwtRedisRepository jwtRedisRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    final User USER = new User(
            "test_login",
            "test_password",
            "test_email@test.com",
            "test_name"
    );

    final User USER_2 = new User(
            "test_login_2",
            "test_password_2",
            "test_email_2@test.com",
            "test_name_2"
    );

    final User USER_3 = new User(
            "test_login_3",
            "test_password_3",
            "test_email_3@test.com",
            "test_name_3"
    );

    private void createDirectoryRedis(String systemName, String path, List<String> userLogins) {
        directoryRedisRepository.save(new DirectoryRedis(
                USER.getLogin(),
                "ROLE_USER",
                systemName,
                systemName,
                path,
                userLogins
        ));
    }

    private void createDirectoryRedis(String login, String systemName, String path, List<String> userLogins) {
        directoryRedisRepository.save(new DirectoryRedis(
                login,
                "ROLE_USER",
                systemName,
                systemName,
                path,
                userLogins
        ));
    }

    private void createFileRedis(String systemName, String path, List<String> userLogins) {
        fileRedisRepository.save(new FileRedis(
                USER.getLogin(),
                "ROLE_USER",
                systemName,
                systemName,
                path,
                userLogins
        ));
    }

    private void createRightFileObject(String systemName, String login) {
        rightUserFileSystemObjectRedisRepository.save(new RightUserFileSystemObjectRedis(
                systemName, login, List.of(OperationRights.SHOW)));
    }

    @Test
    public void deleteAllFileObjects_success_full() throws Exception {
        String userLogin2 = "test_login_2";
        String userLogin3 = "test_login_3";

        jwtRedisRepository.save(new JwtRedis(userLogin2, "access", "refresh"));

        userRepository.save(USER);

        UUID systemNameDirectory1 = UUID.randomUUID();
        UUID systemNameDirectory2 = UUID.randomUUID();
        UUID systemNameDirectory3 = UUID.randomUUID();
        UUID systemNameDirectory4 = UUID.randomUUID();
        UUID systemNameFile1 = UUID.randomUUID();
        String idRightDirectory1 = systemNameDirectory1 +
                ManagerRights.SEPARATOR_UNIQUE_KEY.get()
                + userLogin2;
        String idRightDirectory3_1 = systemNameDirectory3 +
                ManagerRights.SEPARATOR_UNIQUE_KEY.get()
                + USER.getLogin();
        String idRightDirectory3_2 = systemNameDirectory3 +
                ManagerRights.SEPARATOR_UNIQUE_KEY.get()
                + userLogin3;
        String idRightDirectory4_1 = systemNameDirectory4 +
                ManagerRights.SEPARATOR_UNIQUE_KEY.get()
                + USER.getLogin();
        String idRightFile1 = systemNameFile1 +
                ManagerRights.SEPARATOR_UNIQUE_KEY.get()
                + userLogin2;

        Path pathDirectory1 = Path.of(rootPath.getRootPath(), systemNameDirectory1.toString());
        Path pathDirectory2 = Path.of(rootPath.getRootPath(),
                systemNameDirectory1.toString(), systemNameDirectory2.toString());
        Path pathFile1 = Path.of(rootPath.getRootPath(), systemNameFile1.toString());

        createDirectoryRedis(systemNameDirectory1.toString(), pathDirectory1.toString(), new ArrayList<>());
        createDirectoryRedis(systemNameDirectory2.toString(), pathDirectory2.toString(), new ArrayList<>());
        createDirectoryRedis(
                userLogin2,
                systemNameDirectory3.toString(),
                systemNameDirectory3.toString(),
                List.of(USER.getLogin(), userLogin3)
        );
        createDirectoryRedis(
                userLogin2,
                systemNameDirectory4.toString(),
                systemNameDirectory4.toString(),
                List.of(USER.getLogin())
        );
        createFileRedis(systemNameFile1.toString(), pathFile1.toString(), new ArrayList<>());
        createRightFileObject(idRightDirectory1, userLogin2);
        createRightFileObject(idRightDirectory3_1, USER.getLogin());
        createRightFileObject(idRightDirectory3_2, userLogin3);
        createRightFileObject(idRightFile1, userLogin2);

        Files.createDirectory(pathDirectory1);
        Files.createDirectory(pathDirectory2);
        Files.createFile(pathFile1);

        UserDeleteEvent userDeleteEvent = new UserDeleteEvent(userEventPublisher);
        userDeleteEvent.setUserLogin(USER.getLogin());

        Assertions.assertDoesNotThrow(() -> userDeleteEventHandler.onApplicationEvent(userDeleteEvent));
        Assertions.assertFalse(Files.exists(pathDirectory1));
        Assertions.assertFalse(Files.exists(pathDirectory2));
        Assertions.assertFalse(Files.exists(pathFile1));
        Assertions.assertFalse(directoryRedisRepository.existsById(systemNameDirectory1.toString()));
        Assertions.assertFalse(directoryRedisRepository.existsById(systemNameDirectory2.toString()));
        Assertions.assertFalse(fileRedisRepository.existsById(systemNameFile1.toString()));
        Assertions.assertFalse(rightUserFileSystemObjectRedisRepository.existsById(idRightDirectory1));
        Assertions.assertFalse(rightUserFileSystemObjectRedisRepository.existsById(idRightDirectory3_1));
        Assertions.assertTrue(rightUserFileSystemObjectRedisRepository.existsById(idRightDirectory3_2));
        Assertions.assertFalse(rightUserFileSystemObjectRedisRepository.existsById(idRightFile1));
        Assertions.assertFalse(rightUserFileSystemObjectRedisRepository.existsById(idRightDirectory4_1));

        Optional<DirectoryRedis> directoryRedis = directoryRedisRepository.findById(systemNameDirectory3.toString());
        Assertions.assertTrue(directoryRedis.isPresent());
        List<String> userLoginsDirectory3 = directoryRedis
                .get()
                .getUserLogins();
        Assertions.assertNotNull(userLoginsDirectory3);
        Assertions.assertFalse(userLoginsDirectory3.contains(USER.getLogin()));

        jwtRedisRepository.deleteById(userLogin2);
        directoryRedisRepository.deleteById(systemNameDirectory3.toString());
        rightUserFileSystemObjectRedisRepository.deleteById(idRightDirectory3_2);
    }

    @Test
    public void deleteAllFileObjects_success_full_transferredFileObjects() {
        String userLogin3 = "user_login_3";
        User user1 = userRepository.save(USER);
        User user2 = userRepository.save(USER_2);
        UserSettings userSettings = new UserSettings(user1, user2);
        userSettings.setIsDeletingFileObjects(false);
        userSettingsRepository.save(userSettings);

        String systemNameDirectory1 = UUID.randomUUID().toString();
        String systemNameDirectory2 = UUID.randomUUID().toString();
        String systemNameDirectory3 = UUID.randomUUID().toString();
        String systemNameDirectory4 = UUID.randomUUID().toString();
        String systemNameDirectory5 = UUID.randomUUID().toString();
        String systemNameFile1 = UUID.randomUUID().toString();
        String idRightDirectory1_1 = systemNameDirectory1 +
                ManagerRights.SEPARATOR_UNIQUE_KEY.get()
                + userLogin3;
        String idRightDirectory3_1 = systemNameDirectory3 +
                ManagerRights.SEPARATOR_UNIQUE_KEY.get()
                + user1.getLogin();
        String idRightDirectory4_1 = systemNameDirectory4 +
                ManagerRights.SEPARATOR_UNIQUE_KEY.get()
                + user2.getLogin();
        String idRightDirectory5_1 = systemNameDirectory5 +
                ManagerRights.SEPARATOR_UNIQUE_KEY.get()
                + user1.getLogin();

        createDirectoryRedis(systemNameDirectory1, systemNameDirectory1, List.of(userLogin3));
        createDirectoryRedis(systemNameDirectory2, systemNameDirectory2, new ArrayList<>());
        createDirectoryRedis(user2.getLogin(), systemNameDirectory3, systemNameDirectory3, List.of(user1.getLogin()));
        createDirectoryRedis(systemNameDirectory4, systemNameDirectory4, List.of(user2.getLogin()));
        createDirectoryRedis(userLogin3, systemNameDirectory5, systemNameDirectory5, List.of(user1.getLogin()));
        createFileRedis(systemNameFile1, systemNameFile1, new ArrayList<>());
        createRightFileObject(idRightDirectory1_1, userLogin3);
        createRightFileObject(idRightDirectory3_1, user1.getLogin());
        createRightFileObject(idRightDirectory4_1, user2.getLogin());
        createRightFileObject(idRightDirectory5_1, user1.getLogin());

        UserDeleteEvent userDeleteEvent = new UserDeleteEvent(userEventPublisher);
        userDeleteEvent.setUserLogin(USER.getLogin());

        Assertions.assertDoesNotThrow(() -> userDeleteEventHandler.onApplicationEvent(userDeleteEvent));

        Optional<DirectoryRedis> directoryRedis1 = directoryRedisRepository.findById(systemNameDirectory1);
        Optional<DirectoryRedis> directoryRedis2 = directoryRedisRepository.findById(systemNameDirectory2);
        Optional<DirectoryRedis> directoryRedis3 = directoryRedisRepository.findById(systemNameDirectory3);
        Optional<DirectoryRedis> directoryRedis4 = directoryRedisRepository.findById(systemNameDirectory4);
        Optional<DirectoryRedis> directoryRedis5 = directoryRedisRepository.findById(systemNameDirectory5);
        Optional<FileRedis> fileRedis1 = fileRedisRepository.findById(systemNameFile1);

        Assertions.assertTrue(directoryRedis1.isPresent());
        Assertions.assertTrue(directoryRedis2.isPresent());
        Assertions.assertTrue(directoryRedis3.isPresent());
        Assertions.assertTrue(directoryRedis4.isPresent());
        Assertions.assertTrue(directoryRedis5.isPresent());
        Assertions.assertTrue(fileRedis1.isPresent());

        Assertions.assertEquals(directoryRedis1.get().getLogin(), user2.getLogin());
        Assertions.assertTrue(directoryRedis1.get().getUserLogins().contains(userLogin3));
        Assertions.assertEquals(directoryRedis2.get().getLogin(), user2.getLogin());
        Assertions.assertEquals(directoryRedis3.get().getLogin(), user2.getLogin());
        Assertions.assertEquals(directoryRedis4.get().getLogin(), user2.getLogin());
        Assertions.assertTrue(directoryRedis5.get().getUserLogins().contains(user2.getLogin()));
        Assertions.assertTrue(rightUserFileSystemObjectRedisRepository.existsById(idRightDirectory1_1));
        Assertions.assertFalse(rightUserFileSystemObjectRedisRepository.existsById(idRightDirectory3_1));
        Assertions.assertFalse(rightUserFileSystemObjectRedisRepository.existsById(idRightDirectory4_1));
        Assertions.assertFalse(rightUserFileSystemObjectRedisRepository.existsById(idRightDirectory5_1));
        Assertions.assertTrue(rightUserFileSystemObjectRedisRepository.existsById(
                systemNameDirectory5 +
                        ManagerRights.SEPARATOR_UNIQUE_KEY.get()
                        + user2.getLogin()));
        Assertions.assertNull(directoryRedis3.get().getUserLogins());
        Assertions.assertNull(directoryRedis4.get().getUserLogins());
        Assertions.assertEquals(fileRedis1.get().getLogin(), user2.getLogin());

        directoryRedisRepository.deleteAllById(List.of(
                systemNameDirectory1, systemNameDirectory2, systemNameDirectory3));
        fileRedisRepository.deleteById(systemNameFile1);
    }

    @Test
    public void deleteAllFileObjects_success_full_fileObjectsExclusions() throws Exception {
        User user2 = userRepository.save(USER_2);
        User user3 = userRepository.save(USER_3);
        UserSettings userSettings = new UserSettings(user2, user3);

        userSettings.setIsDeletingFileObjects(false);
        userSettingsRepository.save(userSettings);

        UUID systemNameDirectory1 = UUID.randomUUID();
        UUID systemNameDirectory2 = UUID.randomUUID();
        UUID systemNameDirectory3 = UUID.randomUUID();

        Path pathDirectory1 = Path.of(rootPath.getRootPath(), systemNameDirectory1.toString());
        Path pathDirectory2 = Path.of(rootPath.getRootPath(), systemNameDirectory2.toString());
        Path pathDirectory3 = Path.of(rootPath.getRootPath(),
                systemNameDirectory1.toString(), systemNameDirectory3.toString());

        Files.createDirectory(pathDirectory1);
        Files.createDirectory(pathDirectory2);
        Files.createDirectory(pathDirectory3);

        createDirectoryRedis(user2.getLogin(), systemNameDirectory1.toString(),
                pathDirectory1.toString(), new ArrayList<>());
        createDirectoryRedis(user2.getLogin(), systemNameDirectory2.toString(),
                pathDirectory2.toString(), new ArrayList<>());
        createDirectoryRedis(user2.getLogin(), systemNameDirectory3.toString(),
                pathDirectory3.toString(), new ArrayList<>());

        userFileObjectsExclusionRepository.save(new UserFileObjectsExclusion(systemNameDirectory1, user2));

        UserDeleteEvent userDeleteEvent = new UserDeleteEvent(userEventPublisher);
        userDeleteEvent.setUserLogin(user2.getLogin());

        Assertions.assertDoesNotThrow(() -> userDeleteEventHandler.onApplicationEvent(userDeleteEvent));
        Assertions.assertFalse(Files.exists(pathDirectory1));
        Assertions.assertFalse(Files.exists(pathDirectory3));
        Assertions.assertFalse(directoryRedisRepository.existsById(systemNameDirectory1.toString()));
        Assertions.assertFalse(directoryRedisRepository.existsById(systemNameDirectory3.toString()));
    }

}
