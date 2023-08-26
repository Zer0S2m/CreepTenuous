package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.common.enums.ManagerRights;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.models.JwtRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.JwtRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.RightUserFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceControlUserRights;
import com.zer0s2m.creeptenuous.services.redis.resources.ServiceRedisManagerResourcesImpl;
import com.zer0s2m.creeptenuous.services.redis.security.ServiceControlUserRightsImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceRedis;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest(classes = {
        ServiceControlUserRightsImpl.class,
        DirectoryRedisRepository.class,
        FileRedisRepository.class,
        RightUserFileSystemObjectRedisRepository.class,
        JwtRedisRepository.class,
        ServiceRedisManagerResourcesImpl.class,
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceControlUserRightsTests {

    @Autowired
    private ServiceControlUserRights serviceControlUserRights;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @Autowired
    private JwtRedisRepository jwtRedisRepository;

    @Autowired
    private RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    private final String userLogin = "user_login";

    private void createDirectoryRedisRepository(
            final String userLogin, final String systemName, List<String> userLogins) {
        directoryRedisRepository.save(new DirectoryRedis(
                userLogin,
                "USER_ROLE",
                systemName,
                systemName,
                systemName,
                userLogins
        ));
    }

    private void createFileRedisRepository(
            final String userLogin, final String systemName, List<String> userLogins) {
        fileRedisRepository.save(new FileRedis(
                userLogin,
                "USER_ROLE",
                systemName,
                systemName,
                systemName,
                userLogins
        ));
    }

    @Test
    public void removeFileSystemObjects_success() {
        final String systemNameFolder = UUID.randomUUID().toString();
        final String systemNameFile = UUID.randomUUID().toString();

        createDirectoryRedisRepository(userLogin, systemNameFolder, new ArrayList<>());
        createFileRedisRepository(userLogin, systemNameFile, new ArrayList<>());

        Assertions.assertDoesNotThrow(() -> serviceControlUserRights.removeFileSystemObjects(userLogin));
        Assertions.assertFalse(directoryRedisRepository.existsById(systemNameFolder));
        Assertions.assertFalse(fileRedisRepository.existsById(systemNameFile));
    }

    @Test
    public void removeGrantedPermissionsForUser_success() {
        final String userLogin2 = "user_login_2";
        final JwtRedis jwtRedis = jwtRedisRepository.save(
                new JwtRedis(userLogin2, "accessToken", "refreshToken"));
        final String systemNameFolder = UUID.randomUUID().toString();
        final String systemNameFile = UUID.randomUUID().toString();

        rightUserFileSystemObjectRedisRepository.save(new RightUserFileSystemObjectRedis(
                systemNameFolder +
                        ManagerRights.SEPARATOR_UNIQUE_KEY.get() +
                        userLogin2,
                userLogin2,
                List.of(OperationRights.SHOW)
        ));
        rightUserFileSystemObjectRedisRepository.save(new RightUserFileSystemObjectRedis(
                systemNameFile +
                        ManagerRights.SEPARATOR_UNIQUE_KEY.get() +
                        userLogin2,
                userLogin2,
                List.of(OperationRights.SHOW)
        ));

        createDirectoryRedisRepository(userLogin, systemNameFolder, List.of(userLogin2));
        createFileRedisRepository(userLogin, systemNameFile, List.of(userLogin2));

        Assertions.assertDoesNotThrow(() -> serviceControlUserRights.removeGrantedPermissionsForUser(userLogin));
        Assertions.assertFalse(rightUserFileSystemObjectRedisRepository
                .existsById(systemNameFolder + ManagerRights.SEPARATOR_UNIQUE_KEY.get() + userLogin2));
        Assertions.assertFalse(rightUserFileSystemObjectRedisRepository
                .existsById(systemNameFile + ManagerRights.SEPARATOR_UNIQUE_KEY.get() + userLogin2));

        jwtRedisRepository.delete(jwtRedis);
        directoryRedisRepository.deleteById(systemNameFolder);
        fileRedisRepository.deleteById(systemNameFile);
    }

    @Test
    public void removeGrantedPermissionsForUser_success_setIsDistribution() {
        serviceControlUserRights.setIsDistribution(true);

        final String userLogin2 = "user_login_2";
        final JwtRedis jwtRedis = jwtRedisRepository.save(
                new JwtRedis(userLogin2, "accessToken", "refreshToken"));
        final String systemNameFolder = UUID.randomUUID().toString();
        final String systemNameFile = UUID.randomUUID().toString();
        final String idRightFolder = systemNameFolder +
                ManagerRights.SEPARATOR_UNIQUE_KEY.get() +
                userLogin2;
        final String idRightFile = systemNameFile +
                ManagerRights.SEPARATOR_UNIQUE_KEY.get() +
                userLogin2;

        serviceControlUserRights.setFileObjectsExclusions(List.of(UUID.fromString(systemNameFolder)));

        rightUserFileSystemObjectRedisRepository.save(new RightUserFileSystemObjectRedis(
                idRightFolder,
                userLogin2,
                List.of(OperationRights.SHOW)
        ));
        rightUserFileSystemObjectRedisRepository.save(new RightUserFileSystemObjectRedis(
                idRightFile,
                userLogin2,
                List.of(OperationRights.SHOW)
        ));

        createDirectoryRedisRepository(userLogin, systemNameFolder, List.of(userLogin2));
        createFileRedisRepository(userLogin, systemNameFile, List.of(userLogin2));

        Assertions.assertDoesNotThrow(() -> serviceControlUserRights.removeGrantedPermissionsForUser(userLogin));
        Assertions.assertFalse(rightUserFileSystemObjectRedisRepository
                .existsById(idRightFolder));
        Assertions.assertTrue(rightUserFileSystemObjectRedisRepository
                .existsById(idRightFile));

        serviceControlUserRights.setIsDistribution(false);
        serviceControlUserRights.setFileObjectsExclusions(null);

        jwtRedisRepository.delete(jwtRedis);
    }

    @Test
    public void removeAssignedPermissionsForUser_success() {
        final String userLogin2 = "user_login_2";
        final String systemNameFolder = UUID.randomUUID().toString();
        final String systemNameFile = UUID.randomUUID().toString();

        rightUserFileSystemObjectRedisRepository.save(new RightUserFileSystemObjectRedis(
                systemNameFolder +
                        ManagerRights.SEPARATOR_UNIQUE_KEY.get() +
                        userLogin,
                userLogin,
                List.of(OperationRights.SHOW)
        ));
        rightUserFileSystemObjectRedisRepository.save(new RightUserFileSystemObjectRedis(
                systemNameFile +
                        ManagerRights.SEPARATOR_UNIQUE_KEY.get() +
                        userLogin,
                userLogin,
                List.of(OperationRights.SHOW)
        ));

        createDirectoryRedisRepository(userLogin2, systemNameFolder, List.of(userLogin));
        createFileRedisRepository(userLogin2, systemNameFile, List.of(userLogin));

        Assertions.assertDoesNotThrow(() -> serviceControlUserRights.removeAssignedPermissionsForUser(userLogin));

        Optional<DirectoryRedis> directoryRedis = directoryRedisRepository.findById(systemNameFolder);
        Optional<FileRedis> fileRedis = fileRedisRepository.findById(systemNameFile);
        Assertions.assertTrue(directoryRedis.isPresent());
        Assertions.assertTrue(fileRedis.isPresent());

        final List<String> userLoginDirectory = directoryRedis
                .get()
                .getUserLogins();
        final List<String> userLoginFile = fileRedis
                .get()
                .getUserLogins();
        Assertions.assertTrue(userLoginDirectory == null || userLoginDirectory.size() == 0);
        Assertions.assertTrue(userLoginFile == null || userLoginFile.size() == 0);

        directoryRedisRepository.deleteById(systemNameFolder);
        fileRedisRepository.deleteById(systemNameFile);
    }

    @Test
    public void removeAssignedPermissionsForUser_success_setIsDistribution() {
        serviceControlUserRights.setIsDistribution(true);

        final String userLogin2 = "user_login_2";
        final String systemNameFolder = UUID.randomUUID().toString();
        final String systemNameFile = UUID.randomUUID().toString();
        final String idRightFolder = systemNameFolder + ManagerRights.SEPARATOR_UNIQUE_KEY.get() + userLogin;
        final String idRightFile = systemNameFile + ManagerRights.SEPARATOR_UNIQUE_KEY.get() + userLogin;

        serviceControlUserRights.setFileObjectsExclusions(List.of(UUID.fromString(systemNameFolder)));

        rightUserFileSystemObjectRedisRepository.save(new RightUserFileSystemObjectRedis(
                idRightFolder, userLogin, List.of(OperationRights.SHOW)
        ));
        rightUserFileSystemObjectRedisRepository.save(new RightUserFileSystemObjectRedis(
                idRightFile, userLogin, List.of(OperationRights.SHOW)
        ));

        createDirectoryRedisRepository(userLogin2, systemNameFolder, List.of(userLogin));
        createFileRedisRepository(userLogin2, systemNameFile, List.of(userLogin));

        Assertions.assertDoesNotThrow(() -> serviceControlUserRights.removeAssignedPermissionsForUser(userLogin));

        Optional<DirectoryRedis> directoryRedis = directoryRedisRepository.findById(systemNameFolder);
        Optional<FileRedis> fileRedis = fileRedisRepository.findById(systemNameFile);
        Assertions.assertTrue(directoryRedis.isPresent());
        Assertions.assertTrue(fileRedis.isPresent());

        final List<String> userLoginDirectory = directoryRedis
                .get()
                .getUserLogins();
        final List<String> userLoginFile = fileRedis
                .get()
                .getUserLogins();

        Assertions.assertTrue(userLoginDirectory == null || userLoginDirectory.size() == 0);
        Assertions.assertTrue(userLoginFile == null || userLoginFile.size() >= 1);

        directoryRedisRepository.deleteById(systemNameFolder);
        fileRedisRepository.deleteById(systemNameFile);
        rightUserFileSystemObjectRedisRepository.deleteById(idRightFile);

        serviceControlUserRights.setIsDistribution(false);
        serviceControlUserRights.setFileObjectsExclusions(null);
    }

    @Test
    public void removeJwtTokensFotUser_success() {
        jwtRedisRepository.save(new JwtRedis(
                userLogin, "accessToken", "refreshToken"));

        Assertions.assertDoesNotThrow(() -> serviceControlUserRights.removeJwtTokensFotUser(userLogin));
        Assertions.assertFalse(jwtRedisRepository.existsById(userLogin));
    }

    @Test
    public void removeFileSystemObjectBySystemNames_success() {
        final UUID systemNameFolder1 = UUID.randomUUID();
        final UUID systemNameFolder2 = UUID.randomUUID();
        final UUID systemNameFile1 = UUID.randomUUID();
        final UUID systemNameFile2 = UUID.randomUUID();

        createDirectoryRedisRepository(userLogin, systemNameFolder1.toString(), new ArrayList<>());
        createDirectoryRedisRepository(userLogin, systemNameFolder2.toString(), new ArrayList<>());
        createFileRedisRepository(userLogin, systemNameFile1.toString(), new ArrayList<>());
        createFileRedisRepository(userLogin, systemNameFile2.toString(), new ArrayList<>());

        Assertions.assertDoesNotThrow(
                () -> serviceControlUserRights.removeFileSystemObjectsBySystemNames(
                        userLogin, List.of(systemNameFolder1, systemNameFile1)));
        Assertions.assertFalse(directoryRedisRepository.existsById(systemNameFolder1.toString()));
        Assertions.assertTrue(directoryRedisRepository.existsById(systemNameFolder2.toString()));
        Assertions.assertFalse(fileRedisRepository.existsById(systemNameFile1.toString()));
        Assertions.assertTrue(fileRedisRepository.existsById(systemNameFile2.toString()));

        directoryRedisRepository.deleteById(systemNameFolder1.toString());
        fileRedisRepository.deleteById(systemNameFile2.toString());
    }

    @Test
    public void migrateFileSystemObjects_success() {
        final String systemNameFolder1 = UUID.randomUUID().toString();
        final String systemNameFile1 = UUID.randomUUID().toString();

        createDirectoryRedisRepository(userLogin, systemNameFolder1, new ArrayList<>());
        createFileRedisRepository(userLogin, systemNameFile1, new ArrayList<>());

        Assertions.assertDoesNotThrow(
                () -> serviceControlUserRights.migrateFileSystemObjects(userLogin, "user_login_2"));

        Optional<DirectoryRedis> directoryRedis = directoryRedisRepository.findById(systemNameFolder1);
        Optional<FileRedis> fileRedis = fileRedisRepository.findById(systemNameFile1);

        Assertions.assertTrue(directoryRedis.isPresent());
        Assertions.assertTrue(fileRedis.isPresent());
        Assertions.assertEquals("user_login_2", directoryRedis.get().getLogin());
        Assertions.assertEquals("user_login_2", fileRedis.get().getLogin());

        directoryRedisRepository.deleteById(systemNameFolder1);
        fileRedisRepository.deleteById(systemNameFile1);
    }

    @Test
    public void migrateAssignedPermissionsForUser_success() {
        String userLogin2 = "user_login_2";
        String systemNameFolder1 = UUID.randomUUID().toString();
        String systemNameFile1 = UUID.randomUUID().toString();
        String idRightFolder = systemNameFolder1 + ManagerRights.SEPARATOR_UNIQUE_KEY.get() + userLogin;
        String idNewRightFolder = systemNameFolder1 + ManagerRights.SEPARATOR_UNIQUE_KEY.get() + userLogin2;
        String idRightFile = systemNameFile1 + ManagerRights.SEPARATOR_UNIQUE_KEY.get() + userLogin;
        String idNewRightFile = systemNameFile1 + ManagerRights.SEPARATOR_UNIQUE_KEY.get() + userLogin2;

        rightUserFileSystemObjectRedisRepository.save(new RightUserFileSystemObjectRedis(
                idRightFolder, userLogin, List.of(OperationRights.SHOW)));
        rightUserFileSystemObjectRedisRepository.save(new RightUserFileSystemObjectRedis(
                idRightFile, userLogin, List.of(OperationRights.SHOW)));

        createDirectoryRedisRepository(userLogin, systemNameFolder1, new ArrayList<>());
        createFileRedisRepository(userLogin, systemNameFile1, new ArrayList<>());

        Assertions.assertDoesNotThrow(
                () -> serviceControlUserRights.migrateAssignedPermissionsForUser(
                        userLogin, userLogin2));
        Assertions.assertTrue(
                rightUserFileSystemObjectRedisRepository.existsById(idNewRightFolder));
        Assertions.assertTrue(
                rightUserFileSystemObjectRedisRepository.existsById(idNewRightFile));

        directoryRedisRepository.deleteById(systemNameFolder1);
        fileRedisRepository.deleteById(systemNameFile1);
        rightUserFileSystemObjectRedisRepository.deleteAllById(List.of(idNewRightFolder, idNewRightFile));
    }

}
