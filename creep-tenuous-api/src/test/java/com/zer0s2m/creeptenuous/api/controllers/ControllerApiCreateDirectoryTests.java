package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.data.DataCreateDirectoryApi;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.http.ResponseCreateDirectoryApi;
import com.zer0s2m.creeptenuous.common.http.ResponseError;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FrozenFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.RightUserFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.core.services.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagControllerApi;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileSystemUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiCreateDirectoryTests {

    Logger logger = LogManager.getLogger(ControllerApiCreateDirectoryTests.class);

    private final ServiceBuildDirectoryPath serviceBuildDirectoryPath = new ServiceBuildDirectoryPath();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    @Autowired
    private FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    DataCreateDirectoryApi RECORD_1 = new DataCreateDirectoryApi(
            new ArrayList<>(),
            new ArrayList<>(),
            UUID.randomUUID().toString()
    );

    @Test
    public void createDirectory_success() throws Exception {
        MvcResult result = this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(RECORD_1))
                )
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ResponseCreateDirectoryApi response = new ObjectMapper().readValue(json, ResponseCreateDirectoryApi.class);

        Path newFolder = Path.of(serviceBuildDirectoryPath.build(
                List.of(response.systemDirectoryName())
        ));
        Assertions.assertTrue(Files.exists(newFolder));
        UtilsActionForFiles.deleteFileAndWriteLog(newFolder, logger);
    }

    @Deprecated
    public void createDirectory_fail_directoryExists() throws Exception {
        UtilsActionForFiles.createDirectories(List.of("test_folder1"), serviceBuildDirectoryPath, logger);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(RECORD_1))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new ResponseError(
                                        Directory.DIRECTORY_EXISTS.get(),
                                        HttpStatus.BAD_REQUEST.value()
                                )
                        )
                ));

        Path newFolder = Path.of(serviceBuildDirectoryPath.build(List.of("test_folder1")));
        UtilsActionForFiles.deleteFileAndWriteLog(newFolder, logger);
    }

    @Test
    public void createDirectory_fail_invalidPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataCreateDirectoryApi(
                                Arrays.asList("invalid", "path", "directory"),
                                Arrays.asList("invalid", "path", "directory"),
                                "test_folder1"
                        )))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void createDirectory_fail_notFileName() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataCreateDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                ""
                        )))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createDirectory_fail_forbidden() throws Exception {
        DataCreateDirectoryApi dataCreateFileApi = new DataCreateDirectoryApi(
                List.of("testDirectory"),
                List.of("testDirectory"),
                "testDirectory");

        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectory",
                "testDirectory",
                "testDirectory",
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataCreateFileApi))
                )
                .andExpect(status().isForbidden());

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void createDirectory_success_forbidden() throws Exception {
        UtilsActionForFiles.createDirectories(
                List.of("testDirectory"),
                serviceBuildDirectoryPath,
                logger);

        DataCreateDirectoryApi dataCreateDirectoryApi = new DataCreateDirectoryApi(
                List.of("testDirectory"),
                List.of("testDirectory"),
                "testDirectory");
        RightUserFileSystemObjectRedis right = new RightUserFileSystemObjectRedis(
                "testDirectory" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.CREATE));
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                UtilsAuthAction.ROLE_USER,
                "testDirectory",
                "testDirectory",
                "testDirectory",
                List.of(UtilsAuthAction.LOGIN));

        rightUserFileSystemObjectRedisRepository.save(right);
        directoryRedisRepository.save(directoryRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataCreateDirectoryApi))
                )
                .andExpect(status().isCreated());

        directoryRedisRepository.delete(directoryRedis);
        rightUserFileSystemObjectRedisRepository.delete(right);

        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(List.of("testDirectory"))));
    }

    @Test
    public void createDirectory_fail_isFrozenDirectories() throws Exception {
        UtilsActionForFiles.createDirectories(
                List.of("testDirectory"),
                serviceBuildDirectoryPath,
                logger);

        DataCreateDirectoryApi dataCreateDirectoryApi = new DataCreateDirectoryApi(
                List.of("testDirectory"),
                List.of("testDirectory"),
                "testDirectory");

        RightUserFileSystemObjectRedis right = new RightUserFileSystemObjectRedis(
                "testDirectory" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.CREATE, OperationRights.SHOW));
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                UtilsAuthAction.ROLE_USER,
                "testDirectory",
                "testDirectory",
                serviceBuildDirectoryPath.build(List.of("testDirectory")),
                List.of(UtilsAuthAction.LOGIN));
        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                "testDirectory");

        rightUserFileSystemObjectRedisRepository.save(right);
        directoryRedisRepository.save(directoryRedis);
        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataCreateDirectoryApi))
                )
                .andExpect(status().isBadRequest());

        directoryRedisRepository.delete(directoryRedis);
        rightUserFileSystemObjectRedisRepository.delete(right);
        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);

        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(List.of("testDirectory"))));
    }

}
