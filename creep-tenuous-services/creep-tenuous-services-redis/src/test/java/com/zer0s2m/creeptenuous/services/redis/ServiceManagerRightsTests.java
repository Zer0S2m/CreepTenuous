package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.containers.ContainerGrantedRight;
import com.zer0s2m.creeptenuous.common.enums.ManagerRights;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.*;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.models.JwtRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repository.*;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.security.jwt.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.resources.ServiceRedisManagerResourcesImpl;
import com.zer0s2m.creeptenuous.services.redis.security.ServiceManagerRightsImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceRedis;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest(classes = {
        DirectoryRedisRepository.class,
        FileRedisRepository.class,
        RightUserFileSystemObjectRedisRepository.class,
        JwtRedisRepository.class,
        FrozenFileSystemObjectRedisRepository.class,
        ServiceRedisManagerResourcesImpl.class,
        ServiceManagerRightsImpl.class,
        JwtProvider.class
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagServiceRedis
@ContextConfiguration(classes = { ConfigServices.class })
public class ServiceManagerRightsTests {

    @Autowired
    private ServiceManagerRights serviceManagerRights;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @Autowired
    private JwtRedisRepository jwtRedisRepository;

    @Autowired
    private RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    private final RootPath rootPath = new RootPath();

    final String systemName = "system_name_1";

    private final String ownerUserLogin = UtilsAuthAction.LOGIN;

    private final String otherUserLogin = "test_user_2";

    @BeforeEach
    void setUp() {
        serviceManagerRights.setIsWillBeCreated(false);
        serviceManagerRights.setAccessClaims(UtilsAuthAction.generateAccessToken());
    }

    void prepareJwtRedis() {
        final JwtRedis jwtRedis = new JwtRedis(otherUserLogin, "", "");
        jwtRedisRepository.save(jwtRedis);
    }

    void prepareDirectoryRedis() {
        final DirectoryRedis directoryRedis = new DirectoryRedis(ownerUserLogin, "ROLE_USER", systemName,
                systemName, systemName, new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);
    }

    void prepareFileRedis() {
        final FileRedis fileRedis = new FileRedis(ownerUserLogin, "ROLE_USER", systemName,
                systemName, systemName, new ArrayList<>());
        fileRedisRepository.save(fileRedis);
    }

    @Test
    public void addRights_success_objectDirectoryRedis() {
        prepareJwtRedis();
        prepareDirectoryRedis();

        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedis = serviceManagerRights.buildObj(systemName,
                otherUserLogin, OperationRights.SHOW);

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.addRight(rightUserFileSystemObjectRedis),
                "Exceptions");

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedis);
        jwtRedisRepository.deleteById(otherUserLogin);
        directoryRedisRepository.deleteById(systemName);
    }

    @Test
    public void addRights_success_objectFileRedis() {
        prepareJwtRedis();
        prepareFileRedis();

        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedis = serviceManagerRights.buildObj(systemName,
                otherUserLogin, OperationRights.SHOW);

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.addRight(rightUserFileSystemObjectRedis),
                "Exceptions");

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedis);
        jwtRedisRepository.deleteById(otherUserLogin);
        fileRedisRepository.deleteById(systemName);
    }

    @Test
    public void addRights_success_isExistsRights() {
        prepareJwtRedis();
        prepareDirectoryRedis();

        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedisNoExists = serviceManagerRights.buildObj(
                systemName, otherUserLogin, OperationRights.SHOW);
        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedisExists = serviceManagerRights.buildObj(
                systemName, otherUserLogin, OperationRights.COPY);

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.addRight(rightUserFileSystemObjectRedisNoExists),
                "Exceptions");

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.addRight(rightUserFileSystemObjectRedisExists),
                "Exceptions");

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedisNoExists);
        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedisExists);
        jwtRedisRepository.deleteById(otherUserLogin);
        directoryRedisRepository.deleteById(systemName);
    }

    @Test
    public void addRights_success_isExistsRights_then_rightsIsNull() {
        prepareJwtRedis();
        prepareDirectoryRedis();

        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedis =
                new RightUserFileSystemObjectRedis(systemName + "__" + otherUserLogin, otherUserLogin,
                        null);

        rightUserFileSystemObjectRedisRepository.save(rightUserFileSystemObjectRedis);

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.addRight(rightUserFileSystemObjectRedis),
                "Exceptions");

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedis);
        jwtRedisRepository.deleteById(otherUserLogin);
        directoryRedisRepository.deleteById(systemName);
    }

    @Test
    public void addRights_fail_noChangeRightYourself() {
        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedis = serviceManagerRights.buildObj(systemName,
                ownerUserLogin, OperationRights.SHOW);

        Assertions.assertThrows(
                ChangeRightsYourselfException.class,
                () -> serviceManagerRights.addRight(rightUserFileSystemObjectRedis));

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedis);
    }

    @Test
    public void addRightsFromList_success_objectDirectoryRedis() {
        prepareJwtRedis();
        prepareDirectoryRedis();

        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedis = serviceManagerRights.buildObj(systemName,
                otherUserLogin, OperationRights.SHOW);

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.addRight(
                        List.of(rightUserFileSystemObjectRedis), OperationRights.SHOW),
                "Exceptions");

        Assertions.assertTrue(rightUserFileSystemObjectRedisRepository.existsById(
                systemName + "__" + otherUserLogin));

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedis);
        jwtRedisRepository.deleteById(otherUserLogin);
        directoryRedisRepository.deleteById(systemName);
    }

    @Test
    public void addRightsFromList_success_objectFileRedis() {
        prepareJwtRedis();
        prepareFileRedis();

        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedis = serviceManagerRights.buildObj(
                systemName, otherUserLogin, OperationRights.SHOW);

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.addRight(
                        List.of(rightUserFileSystemObjectRedis), OperationRights.SHOW),
                "Exceptions");

        Assertions.assertTrue(rightUserFileSystemObjectRedisRepository.existsById(
                systemName + "__" + otherUserLogin));

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedis);
        jwtRedisRepository.deleteById(otherUserLogin);
        fileRedisRepository.deleteById(systemName);
    }

    @Test
    public void deleteRights_success() {
        prepareJwtRedis();

        final DirectoryRedis directoryRedis = new DirectoryRedis(ownerUserLogin, "ROLE_USER", systemName,
                systemName, systemName, List.of(otherUserLogin));
        directoryRedisRepository.save(directoryRedis);

        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedisCreate = serviceManagerRights.buildObj(
                systemName + "__" + otherUserLogin, otherUserLogin, OperationRights.SHOW);

        rightUserFileSystemObjectRedisRepository.save(rightUserFileSystemObjectRedisCreate);

        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedisReal = serviceManagerRights.getObj(
                systemName, otherUserLogin);

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.deleteRight(rightUserFileSystemObjectRedisReal, OperationRights.SHOW),
                "Exceptions");

        Assertions.assertFalse(rightUserFileSystemObjectRedisRepository.existsById(
                rightUserFileSystemObjectRedisReal.getFileSystemObject()));

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedisReal);
        jwtRedisRepository.deleteById(otherUserLogin);
        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void deleteRightsFromList_success() {
        prepareJwtRedis();

        final DirectoryRedis directoryRedis = new DirectoryRedis(ownerUserLogin, "ROLE_USER", systemName,
                systemName, systemName, List.of(otherUserLogin));
        directoryRedisRepository.save(directoryRedis);

        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedisCreate = serviceManagerRights.buildObj(
                systemName + "__" + otherUserLogin, otherUserLogin, OperationRights.SHOW);

        rightUserFileSystemObjectRedisRepository.save(rightUserFileSystemObjectRedisCreate);

        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedisReal = serviceManagerRights.getObj(
                systemName, otherUserLogin);

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.deleteRight(
                        List.of(rightUserFileSystemObjectRedisReal), OperationRights.SHOW),
                "Exceptions");

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedisReal);
        jwtRedisRepository.deleteById(otherUserLogin);
        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void deleteRights_fail_noChangeRightYourself() {
        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedis = serviceManagerRights.buildObj(systemName,
                ownerUserLogin, OperationRights.SHOW);

        Assertions.assertThrows(
                ChangeRightsYourselfException.class,
                () -> serviceManagerRights.deleteRight(rightUserFileSystemObjectRedis, OperationRights.SHOW));

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedis);
    }

    @Test
    public void isExistsUser_success() {
        prepareJwtRedis();

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.isExistsUser(otherUserLogin),
                "Not found user");

        jwtRedisRepository.deleteById(otherUserLogin);
    }

    @Test
    public void isExistsUser_fail_notFound() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceManagerRights.isExistsUser("not_exists_user"));
    }

    @Test
    public void isExistsFileSystemObject_success() {
        prepareDirectoryRedis();

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.isExistsFileSystemObject(systemName),
                "Not found file system object");

        directoryRedisRepository.deleteById(systemName);
    }

    @Test
    public void isExistsFileSystemObject_fail_notFound() {
        Assertions.assertThrows(
                NoExistsFileSystemObjectRedisException.class,
                () -> serviceManagerRights.isExistsFileSystemObject("not_exists_object"));
    }

    @Test
    public void checkRightsByOperation_success_objectDirectoryRedis() {
        prepareJwtRedis();
        prepareDirectoryRedis();

        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedis = serviceManagerRights.buildObj(systemName,
                otherUserLogin, OperationRights.SHOW);
        rightUserFileSystemObjectRedisRepository.save(rightUserFileSystemObjectRedis);

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.addRight(rightUserFileSystemObjectRedis),
                "Exceptions");

        Assertions.assertNotNull(serviceManagerRights.getLoginUser());
        serviceManagerRights.setLoginUser(otherUserLogin);
        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.checkRightsByOperation(OperationRights.SHOW, systemName),
                "Exceptions");
        serviceManagerRights.setAccessToken(UtilsAuthAction.generateAccessToken());

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedis);
        jwtRedisRepository.deleteById(otherUserLogin);
        directoryRedisRepository.deleteById(systemName);
    }

    @Test
    public void checkRightsByOperation_success_objectFileRedis() {
        prepareJwtRedis();
        prepareFileRedis();

        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedis = serviceManagerRights.buildObj(systemName,
                otherUserLogin, OperationRights.SHOW);
        rightUserFileSystemObjectRedisRepository.save(rightUserFileSystemObjectRedis);

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.addRight(rightUserFileSystemObjectRedis),
                "Exceptions");

        Assertions.assertNotNull(serviceManagerRights.getLoginUser());
        serviceManagerRights.setLoginUser(otherUserLogin);
        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.checkRightsByOperation(OperationRights.SHOW, systemName),
                "Exceptions");
        serviceManagerRights.setAccessToken(UtilsAuthAction.generateAccessToken());

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedis);
        jwtRedisRepository.deleteById(otherUserLogin);
        fileRedisRepository.deleteById(systemName);
    }

    @Test
    public void checkRightsByOperation_fail_notRights() {
        prepareJwtRedis();
        prepareDirectoryRedis();

        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedis = serviceManagerRights.buildObj(systemName,
                otherUserLogin, OperationRights.SHOW);
        rightUserFileSystemObjectRedisRepository.save(rightUserFileSystemObjectRedis);

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.addRight(rightUserFileSystemObjectRedis),
                "Exceptions");

        Assertions.assertNotNull(serviceManagerRights.getLoginUser());
        serviceManagerRights.setLoginUser(otherUserLogin);
        Assertions.assertThrows(
                NoRightsRedisException.class,
                () -> serviceManagerRights.checkRightsByOperation(OperationRights.COPY, systemName));
        serviceManagerRights.setAccessToken(UtilsAuthAction.generateAccessToken());

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedis);
        jwtRedisRepository.deleteById(otherUserLogin);
        directoryRedisRepository.deleteById(systemName);
    }

    @Test
    public void permissionFiltering_success_objectDirectoryRedis() {
        prepareJwtRedis();
        prepareDirectoryRedis();

        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedis = serviceManagerRights.buildObj(systemName,
                otherUserLogin, OperationRights.SHOW);
        rightUserFileSystemObjectRedisRepository.save(rightUserFileSystemObjectRedis);

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.addRight(rightUserFileSystemObjectRedis),
                "Exceptions");

        Assertions.assertFalse(serviceManagerRights.permissionFiltering(
                List.of(systemName), OperationRights.SHOW).isEmpty());

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedis);
        jwtRedisRepository.deleteById(otherUserLogin);
        directoryRedisRepository.deleteById(systemName);
    }

    @Test
    public void permissionFiltering_success_objectFileRedis() {
        prepareJwtRedis();
        prepareFileRedis();

        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedis = serviceManagerRights.buildObj(systemName,
                otherUserLogin, OperationRights.SHOW);
        rightUserFileSystemObjectRedisRepository.save(rightUserFileSystemObjectRedis);

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.addRight(rightUserFileSystemObjectRedis),
                "Exceptions");

        Assertions.assertFalse(serviceManagerRights.permissionFiltering(
                List.of(systemName), OperationRights.SHOW).isEmpty());

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedis);
        jwtRedisRepository.deleteById(otherUserLogin);
        fileRedisRepository.deleteById(systemName);
    }

    @Test
    public void getGrantedRight_success_objectFile() {
        prepareJwtRedis();
        final FileRedis fileRedis = new FileRedis(ownerUserLogin, "ROLE_USER", systemName,
                systemName, systemName, List.of(otherUserLogin));
        fileRedisRepository.save(fileRedis);

        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedis = serviceManagerRights.buildObj(
                systemName + "__" + otherUserLogin, otherUserLogin, OperationRights.SHOW);
        rightUserFileSystemObjectRedisRepository.save(rightUserFileSystemObjectRedis);

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.getGrantedRight(systemName),
                "Exceptions");

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedis);
        jwtRedisRepository.deleteById(otherUserLogin);
        fileRedisRepository.delete(fileRedis);
    }

    @Test
    public void getGrantedRight_success_objectDirectory() {
        prepareJwtRedis();
        final DirectoryRedis directoryRedis = new DirectoryRedis(ownerUserLogin, "ROLE_USER", systemName,
                systemName, systemName, List.of(otherUserLogin));
        directoryRedisRepository.save(directoryRedis);

        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedis = serviceManagerRights.buildObj(
                systemName + "__" + otherUserLogin, otherUserLogin, OperationRights.SHOW);
        rightUserFileSystemObjectRedisRepository.save(rightUserFileSystemObjectRedis);

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.getGrantedRight(systemName),
                "Exceptions");

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedis);
        jwtRedisRepository.deleteById(otherUserLogin);
        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void getGrantedRight_success_objectDirectoryAndNotUserLogins() {
        prepareJwtRedis();
        prepareDirectoryRedis();

        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedis = serviceManagerRights.buildObj(
                systemName + "__" + otherUserLogin, otherUserLogin, OperationRights.SHOW);
        rightUserFileSystemObjectRedisRepository.save(rightUserFileSystemObjectRedis);

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.getGrantedRight(systemName),
                "Exceptions");

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedis);
        jwtRedisRepository.deleteById(otherUserLogin);
        directoryRedisRepository.deleteById(systemName);
    }

    @Test
    public void getGrantedRight_fail_notObjectInRedis() {
        List<ContainerGrantedRight> rights = serviceManagerRights.getGrantedRight(
                "not_fount_object_in_redis");
        Assertions.assertEquals(0, rights.size());
    }

    @Test
    public void getGrantedRightAll_success() {
        prepareJwtRedis();

        final String systemNameDirectory = UUID.randomUUID().toString();
        final String systemNameFile = UUID.randomUUID().toString();

        final DirectoryRedis directoryRedis = new DirectoryRedis(
                ownerUserLogin,
                "ROLE_USER",
                systemNameDirectory,
                systemNameDirectory,
                systemNameDirectory,
                List.of(otherUserLogin));
        final FileRedis fileRedis = new FileRedis(
                ownerUserLogin,
                "ROLE_USER",
                systemNameFile,
                systemNameFile,
                systemNameFile,
                List.of(otherUserLogin, "user_test_3"));
        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedisDirectory = serviceManagerRights.buildObj(
                systemNameDirectory + "__" + otherUserLogin, otherUserLogin, OperationRights.SHOW);
        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedisFile = serviceManagerRights.buildObj(
                systemNameFile + "__" + otherUserLogin, otherUserLogin, OperationRights.SHOW);
        final RightUserFileSystemObjectRedis rightUserFileSystemObjectRedisFile2 = serviceManagerRights.buildObj(
                systemNameFile + "__" + "user_test_3", "user_test_3", OperationRights.SHOW);
        directoryRedisRepository.save(directoryRedis);
        fileRedisRepository.save(fileRedis);
        rightUserFileSystemObjectRedisRepository.saveAll(List.of(
                rightUserFileSystemObjectRedisDirectory, rightUserFileSystemObjectRedisFile,
                rightUserFileSystemObjectRedisFile2));

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.getGrantedRight(),
                "Exceptions");

        jwtRedisRepository.deleteById(otherUserLogin);
        directoryRedisRepository.delete(directoryRedis);
        fileRedisRepository.delete(fileRedis);
        rightUserFileSystemObjectRedisRepository.deleteAll(List.of(
                rightUserFileSystemObjectRedisDirectory, rightUserFileSystemObjectRedisFile,
                rightUserFileSystemObjectRedisFile2));
    }

    @Test
    public void checkDeletingRightsYourself_fail_notFoundRight() {
        Assertions.assertThrows(
                NoExistsRightException.class,
                () -> serviceManagerRights.checkDeletingRightsYourself(null));
    }

    @Test
    public void checkRightByOperationDirectory_success() throws Exception {
        final JwtRedis jwtRedis = new JwtRedis(ownerUserLogin, "", "");
        jwtRedisRepository.save(jwtRedis);
        serviceManagerRights.setIsDirectory(true);
        serviceManagerRights.setLoginUser(ownerUserLogin);

        final String systemNameDirectory = UUID.randomUUID().toString();
        Path pathDirectory = Path.of(rootPath.getRootPath(), systemNameDirectory);

        Files.createDirectory(pathDirectory);

        final DirectoryRedis directoryRedis = directoryRedisRepository.save(new DirectoryRedis(
                ownerUserLogin,
                "ROLE_USER",
                systemNameDirectory,
                systemNameDirectory,
                pathDirectory.toString(),
                new ArrayList<>()));

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.checkRightByOperationDeleteDirectory(systemNameDirectory));
        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.checkRightByOperationMoveDirectory(systemNameDirectory));
        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.checkRightByOperationCopyDirectory(systemNameDirectory));
        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.checkRightByOperationDownloadDirectory(systemNameDirectory));

        serviceManagerRights.setIsDirectory(false);
        serviceManagerRights.setLoginUser(null);

        directoryRedisRepository.delete(directoryRedis);
        jwtRedisRepository.delete(jwtRedis);
        Files.delete(pathDirectory);
    }

    @Test
    public void setDirectoryPassAccess_success_isDirectory() throws IOException {
        prepareJwtRedis();

        String systemNamePart = UUID.randomUUID().toString();
        String systemNameDirectory = UUID.randomUUID().toString();
        String idRight = systemNamePart + ManagerRights.SEPARATOR_UNIQUE_KEY.get() + otherUserLogin;

        Path partDirectory = Path.of(rootPath.getRootPath(), systemNamePart);
        Path pathDirectory = Path.of(partDirectory.toString(), systemNameDirectory);

        Files.createDirectories(pathDirectory);

        DirectoryRedis directoryRedis = directoryRedisRepository.save(new DirectoryRedis(
                ownerUserLogin,
                "ROLE_USER",
                "test",
                systemNameDirectory,
                pathDirectory.toString(),
                List.of(otherUserLogin)));

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.setDirectoryPassAccess(
                        systemNameDirectory, otherUserLogin));
        Assertions.assertTrue(rightUserFileSystemObjectRedisRepository.existsById(idRight));

        Files.delete(pathDirectory);
        Files.delete(partDirectory);
        directoryRedisRepository.delete(directoryRedis);
        rightUserFileSystemObjectRedisRepository.deleteById(idRight);
    }

    @Test
    public void setDirectoryPassAccess_success_isFile() throws IOException {
        prepareJwtRedis();

        String systemNamePart = UUID.randomUUID().toString();
        String systemNameFile = UUID.randomUUID().toString();
        String idRight = systemNamePart + ManagerRights.SEPARATOR_UNIQUE_KEY.get() + otherUserLogin;

        Path partDirectory = Path.of(rootPath.getRootPath(), systemNamePart);
        Path pathFile = Path.of(partDirectory.toString(), systemNameFile);

        Files.createDirectory(partDirectory);
        Files.createFile(pathFile);

        FileRedis fileRedis = fileRedisRepository.save(new FileRedis(
                ownerUserLogin,
                "ROLE_USER",
                "test",
                systemNameFile,
                pathFile.toString(),
                List.of(otherUserLogin)));

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.setDirectoryPassAccess(
                        systemNameFile, otherUserLogin));
        Assertions.assertTrue(rightUserFileSystemObjectRedisRepository.existsById(idRight));

        Files.delete(pathFile);
        Files.delete(partDirectory);
        fileRedisRepository.delete(fileRedis);
        rightUserFileSystemObjectRedisRepository.deleteById(idRight);
    }

    @Test
    public void setDirectoryPassAccess_fail_notExistsDirectory() {
        prepareJwtRedis();

        String systemNamePart = UUID.randomUUID().toString();
        String systemNameDirectory = UUID.randomUUID().toString();
        String idRight = systemNamePart + ManagerRights.SEPARATOR_UNIQUE_KEY.get() + otherUserLogin;

        Path partDirectory = Path.of(rootPath.getRootPath(), systemNamePart);
        Path pathDirectory = Path.of(partDirectory.toString(), systemNameDirectory);

        DirectoryRedis directoryRedis = directoryRedisRepository.save(new DirectoryRedis(
                ownerUserLogin,
                "ROLE_USER",
                "test",
                systemNameDirectory,
                pathDirectory.toString(),
                List.of(otherUserLogin)));

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.setDirectoryPassAccess(
                        systemNameDirectory, otherUserLogin));
        Assertions.assertFalse(rightUserFileSystemObjectRedisRepository.existsById(idRight));

        directoryRedisRepository.delete(directoryRedis);
        rightUserFileSystemObjectRedisRepository.deleteById(idRight);
    }

}
