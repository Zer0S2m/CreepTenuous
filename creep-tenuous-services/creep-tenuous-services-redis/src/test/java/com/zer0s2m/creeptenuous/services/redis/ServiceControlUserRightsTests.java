package com.zer0s2m.creeptenuous.services.redis;

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

    @Test
    public void removeFileSystemObjects_success() {
        final String systemNameFolder = UUID.randomUUID().toString();
        final String systemNameFile = UUID.randomUUID().toString();

        directoryRedisRepository.save(new DirectoryRedis(
                userLogin,
                "USER_ROLE",
                systemNameFolder,
                systemNameFolder,
                systemNameFolder,
                new ArrayList<>()
        ));
        fileRedisRepository.save(new FileRedis(
                userLogin,
                "USER_ROLE",
                systemNameFile,
                systemNameFile,
                systemNameFile,
                new ArrayList<>()
        ));

        Assertions.assertDoesNotThrow(
                () -> serviceControlUserRights.removeFileSystemObjects(userLogin)
        );
        Assertions.assertFalse(directoryRedisRepository.existsById(systemNameFolder));
        Assertions.assertFalse(fileRedisRepository.existsById(systemNameFile));
    }

    @Test
    public void removeGrantedPermissionsForUser_success() {
        final String userLogin2 = "user_login_2";
        final JwtRedis jwtRedis = jwtRedisRepository.save(new JwtRedis(
                userLogin2, "accessToken", "refreshToken"));
        final String systemNameFolder = UUID.randomUUID().toString();
        final String systemNameFile = UUID.randomUUID().toString();

        rightUserFileSystemObjectRedisRepository.save(new RightUserFileSystemObjectRedis(
                systemNameFolder + "__" + userLogin2, userLogin2, List.of(OperationRights.SHOW)
        ));
        rightUserFileSystemObjectRedisRepository.save(new RightUserFileSystemObjectRedis(
                systemNameFile + "__" + userLogin2, userLogin2, List.of(OperationRights.SHOW)
        ));
        directoryRedisRepository.save(new DirectoryRedis(
                userLogin,
                "USER_ROLE",
                systemNameFolder,
                systemNameFolder,
                systemNameFolder,
                List.of(userLogin2)
        ));
        fileRedisRepository.save(new FileRedis(
                userLogin,
                "USER_ROLE",
                systemNameFile,
                systemNameFile,
                systemNameFile,
                List.of(userLogin2)
        ));

        Assertions.assertDoesNotThrow(
                () -> serviceControlUserRights.removeGrantedPermissionsForUser(userLogin)
        );
        Assertions.assertFalse(rightUserFileSystemObjectRedisRepository
                .existsById(systemNameFolder + "__" + userLogin2));
        Assertions.assertFalse(rightUserFileSystemObjectRedisRepository
                .existsById(systemNameFile + "__" + userLogin2));

        jwtRedisRepository.delete(jwtRedis);
        directoryRedisRepository.deleteById(systemNameFolder);
        fileRedisRepository.deleteById(systemNameFile);
    }

    @Test
    public void removeAssignedPermissionsForUser_success() {
        final String userLogin2 = "user_login_2";
        final String systemNameFolder = UUID.randomUUID().toString();
        final String systemNameFile = UUID.randomUUID().toString();

        rightUserFileSystemObjectRedisRepository.save(new RightUserFileSystemObjectRedis(
                systemNameFolder + "__" + userLogin, userLogin, List.of(OperationRights.SHOW)
        ));
        rightUserFileSystemObjectRedisRepository.save(new RightUserFileSystemObjectRedis(
                systemNameFile + "__" + userLogin, userLogin, List.of(OperationRights.SHOW)
        ));
        directoryRedisRepository.save(new DirectoryRedis(
                userLogin2,
                "USER_ROLE",
                systemNameFolder,
                systemNameFolder,
                systemNameFolder,
                List.of(userLogin)
        ));
        fileRedisRepository.save(new FileRedis(
                userLogin2,
                "USER_ROLE",
                systemNameFile,
                systemNameFile,
                systemNameFile,
                List.of(userLogin)
        ));

        Assertions.assertDoesNotThrow(
                () -> serviceControlUserRights.removeAssignedPermissionsForUser(userLogin)
        );
        final List<String> userLoginDirectory = directoryRedisRepository
                .findById(systemNameFolder)
                .get()
                .getUserLogins();
        Assertions.assertTrue(
                userLoginDirectory == null || userLoginDirectory.size() == 0
        );
        final List<String> userLoginFile = fileRedisRepository
                .findById(systemNameFile)
                .get()
                .getUserLogins();
        Assertions.assertTrue(
                userLoginFile == null || userLoginFile.size() == 0
        );

        directoryRedisRepository.deleteById(systemNameFolder);
        fileRedisRepository.deleteById(systemNameFile);
    }

    @Test
    public void removeJwtTokensFotUser_success() {
        jwtRedisRepository.save(new JwtRedis(
                userLogin, "accessToken", "refreshToken"));

        Assertions.assertDoesNotThrow(
                () -> serviceControlUserRights.removeJwtTokensFotUser(userLogin)
        );
        Assertions.assertFalse(jwtRedisRepository.existsById(userLogin));
    }

}
