package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.data.DataCreateRightUserApi;
import com.zer0s2m.creeptenuous.common.data.DataDeleteRightUserApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.JwtRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.JwtRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.RightUserFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagControllerApi;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiRightUserTests {

    Logger logger = LogManager.getLogger(ControllerApiRightUserTests.class);

    @Autowired
    private ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtRedisRepository jwtRedisRepository;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    DataCreateRightUserApi RECORD_ADD = new DataCreateRightUserApi(
            "testFolder_1",
            "test_user_2",
            OperationRights.SHOW.name()
    );

    DataCreateRightUserApi RECORD_ADD_RIGHTS_YOURSELF = new DataCreateRightUserApi(
            "testFolder_1",
            UtilsAuthAction.LOGIN,
            OperationRights.SHOW.name()
    );

    DataCreateRightUserApi RECORD_FAIL_NOT_FOUND_USER = new DataCreateRightUserApi(
            "testFolder_1",
            "test_user_not_found_fail_test",
            OperationRights.SHOW.name()
    );

    DataDeleteRightUserApi RECORD_DELETE = new DataDeleteRightUserApi(
            "testFolder_1",
            "test_login_2",
            OperationRights.SHOW.name()
    );

    DataDeleteRightUserApi RECORD_FAIL_NOT_RIGHTS = new DataDeleteRightUserApi(
            "testFolder_1",
            UtilsAuthAction.LOGIN,
            OperationRights.MOVE.name()
    );

    @Test
    public void addRight_success() throws Exception {
        Path pathTestFile = UtilsActionForFiles.preparePreliminaryFiles(
                RECORD_ADD.systemName(), new ArrayList<>(), logger, buildDirectoryPath
        );

        final DirectoryRedis directoryRedis = new DirectoryRedis(UtilsAuthAction.LOGIN, "ROLE_USER",
                RECORD_ADD.systemName(), RECORD_ADD.systemName(), pathTestFile.toString(), new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        final JwtRedis jwtRedis = new JwtRedis(RECORD_ADD.loginUser(), "", "");
        jwtRedisRepository.save(jwtRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/user/right")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_ADD))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isCreated());

        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile, logger);
        jwtRedisRepository.delete(jwtRedis);
        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void addRight_fail_notFoundUser() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/user/right")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_FAIL_NOT_FOUND_USER))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void addRight_fail_notAddRightsYourself() throws Exception {
        Path pathTestFile = UtilsActionForFiles.preparePreliminaryFiles(
                RECORD_ADD.systemName(), new ArrayList<>(), logger, buildDirectoryPath
        );

        final DirectoryRedis directoryRedis = new DirectoryRedis(UtilsAuthAction.LOGIN, "ROLE_USER",
                RECORD_ADD.systemName(), RECORD_ADD.systemName(), pathTestFile.toString(), new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        final JwtRedis jwtRedis = new JwtRedis(RECORD_ADD_RIGHTS_YOURSELF.loginUser(), "", "");
        jwtRedisRepository.save(jwtRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/user/right")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_ADD_RIGHTS_YOURSELF))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isBadRequest());

        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile, logger);
        jwtRedisRepository.delete(jwtRedis);
        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void addRight_notFoundObjectInRedis() throws Exception {
        final JwtRedis jwtRedis = new JwtRedis(RECORD_ADD.loginUser(), "", "");
        jwtRedisRepository.save(jwtRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/user/right")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_ADD))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());

        jwtRedisRepository.delete(jwtRedis);
    }

    @Test
    public void deleteRight_success() throws Exception {
        Path pathTestFile = UtilsActionForFiles.preparePreliminaryFiles(
                RECORD_DELETE.systemName(), new ArrayList<>(), logger, buildDirectoryPath
        );

        final DirectoryRedis directoryRedis = new DirectoryRedis(UtilsAuthAction.LOGIN, "ROLE_USER",
                RECORD_DELETE.systemName(), RECORD_DELETE.systemName(),
                pathTestFile.toString(), new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        final JwtRedis jwtRedis = new JwtRedis(RECORD_DELETE.loginUser(), "", "");
        jwtRedisRepository.save(jwtRedis);

        rightUserFileSystemObjectRedisRepository.save(new RightUserFileSystemObjectRedis(
                RECORD_DELETE.systemName() + "__" + RECORD_DELETE.loginUser(),
                RECORD_DELETE.loginUser(),
                List.of(OperationRights.valueOf(RECORD_DELETE.right()))
        ));

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/user/right")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_DELETE))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNoContent());

        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile, logger);
        jwtRedisRepository.delete(jwtRedis);
        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void deleteRight_fail_notRights() throws Exception {
        Path pathTestFile = UtilsActionForFiles.preparePreliminaryFiles(
                RECORD_FAIL_NOT_RIGHTS.systemName(), new ArrayList<>(), logger, buildDirectoryPath
        );

        final DirectoryRedis directoryRedis = new DirectoryRedis("test_user_3", "ROLE_USER",
                RECORD_FAIL_NOT_RIGHTS.systemName(), RECORD_FAIL_NOT_RIGHTS.systemName(),
                pathTestFile.toString(), new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        final JwtRedis jwtRedis = new JwtRedis(RECORD_FAIL_NOT_RIGHTS.loginUser(), "", "");
        jwtRedisRepository.save(jwtRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/user/right")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_FAIL_NOT_RIGHTS))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());

        UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile, logger);
        jwtRedisRepository.delete(jwtRedis);
        directoryRedisRepository.delete(directoryRedis);
    }

}