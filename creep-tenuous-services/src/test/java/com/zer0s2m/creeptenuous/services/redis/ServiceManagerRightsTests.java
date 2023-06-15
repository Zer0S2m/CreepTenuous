package com.zer0s2m.creeptenuous.services.redis;

import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.ChangeRightsYourselfException;
import com.zer0s2m.creeptenuous.common.exceptions.NoExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.common.exceptions.NoRightsRedisException;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.models.JwtRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.JwtRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.RightUserFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.ConfigServices;
import com.zer0s2m.creeptenuous.services.redis.resources.ServiceRedisManagerResourcesImpl;
import com.zer0s2m.creeptenuous.services.redis.security.ServiceManagerRightsImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagServiceRedis;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = {
        DirectoryRedisRepository.class,
        FileRedisRepository.class,
        RightUserFileSystemObjectRedisRepository.class,
        JwtRedisRepository.class,
        ServiceRedisManagerResourcesImpl.class,
        ServiceManagerRightsImpl.class,
        JwtProvider.class,
})
@Transactional
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

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.addRight(rightUserFileSystemObjectRedis),
                "Exceptions");

        Assertions.assertTrue(serviceManagerRights.permissionFiltering(
                List.of(systemName), OperationRights.SHOW).size() >= 1);

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

        Assertions.assertDoesNotThrow(
                () -> serviceManagerRights.addRight(rightUserFileSystemObjectRedis),
                "Exceptions");

        Assertions.assertTrue(serviceManagerRights.permissionFiltering(
                List.of(systemName), OperationRights.SHOW).size() >= 1);

        rightUserFileSystemObjectRedisRepository.delete(rightUserFileSystemObjectRedis);
        jwtRedisRepository.deleteById(otherUserLogin);
        fileRedisRepository.deleteById(systemName);
    }

}
