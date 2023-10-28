package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.data.DataDeleteDirectoryApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FrozenFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.RightUserFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiDeleteDirectoryTests {

    Logger logger = LogManager.getLogger(ControllerApiDeleteDirectoryTests.class);

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

    @Test
    public void deleteDirectory_success() throws Exception {
        DataDeleteDirectoryApi RECORD_1 = new DataDeleteDirectoryApi(
                new ArrayList<>(),
                new ArrayList<>(),
                "test_folder1",
                "test_folder1"
        );

        Path newFolder = UtilsActionForFiles.preparePreliminaryFiles(
                RECORD_1.systemDirectoryName(),
                new ArrayList<>(),
                logger,
                serviceBuildDirectoryPath
        );
        Files.createDirectory(newFolder);
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/directory/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RECORD_1))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNoContent());

        Assertions.assertFalse(Files.exists(newFolder));
    }

    @Test
    public void deleteDirectory_fail_invalidPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/directory/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataDeleteDirectoryApi(
                                Arrays.asList("invalid", "path", "directory"),
                                Arrays.asList("invalid", "path", "directory"),
                                "test_folder1",
                                "test_folder1"
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteDirectory_fail_notFileName() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/directory/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new DataDeleteDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                "",
                                ""
                        )))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteDirectory_fail_forbiddenDirectories() throws Exception {
        DataDeleteDirectoryApi dataDeleteDirectoryApi = new DataDeleteDirectoryApi(
                List.of("testDirectory"), List.of("testDirectory"), "test_folder1", "test_folder1");

        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectory",
                "testDirectory",
                "testDirectory",
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/directory/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataDeleteDirectoryApi))
                )
                .andExpect(status().isForbidden());

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void deleteDirectory_success_forbidden() throws Exception {
        UtilsActionForFiles.createDirectories(
                List.of("testDirectory"),
                serviceBuildDirectoryPath,
                logger);

        DataDeleteDirectoryApi dataDeleteDirectoryApi = new DataDeleteDirectoryApi(
                new ArrayList<>(), new ArrayList<>(), "testDirectory", "testDirectory");

        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                UtilsAuthAction.ROLE_USER,
                "testDirectory",
                "testDirectory",
                serviceBuildDirectoryPath.build(List.of("testDirectory")),
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis right = new RightUserFileSystemObjectRedis(
                "testDirectory" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.DELETE));

        directoryRedisRepository.saveAll(List.of(directoryRedis));
        rightUserFileSystemObjectRedisRepository.saveAll(List.of(right));

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/directory/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataDeleteDirectoryApi))
                )
                .andExpect(status().isNoContent());

        directoryRedisRepository.deleteAll(List.of(directoryRedis));
        rightUserFileSystemObjectRedisRepository.deleteAll(List.of(right));
    }

    @Test
    public void deleteDirectory_fail_isFrozenDirectory() throws Exception {
        UtilsActionForFiles.createDirectories(
                List.of("testDirectory"),
                serviceBuildDirectoryPath,
                logger);

        DataDeleteDirectoryApi dataDeleteDirectoryApi = new DataDeleteDirectoryApi(
                new ArrayList<>(),
                new ArrayList<>(),
                "testDirectory",
                "testDirectory");

        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                UtilsAuthAction.ROLE_USER,
                "testDirectory",
                "testDirectory",
                serviceBuildDirectoryPath.build(List.of("testDirectory")),
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis rightDirectory = new RightUserFileSystemObjectRedis(
                "testDirectory" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.DELETE));
        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                "testDirectory");

        directoryRedisRepository.save(directoryRedis);
        rightUserFileSystemObjectRedisRepository.save(rightDirectory);
        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/directory/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(dataDeleteDirectoryApi))
                )
                .andExpect(status().isBadRequest());

        directoryRedisRepository.delete(directoryRedis);
        rightUserFileSystemObjectRedisRepository.delete(rightDirectory);
        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);

        Files.deleteIfExists(Path.of(serviceBuildDirectoryPath.build(), "testDirectory"));
    }

}
