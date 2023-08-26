package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.data.DataManagerDirectoryApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FrozenFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.RightUserFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagControllerApi;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileSystemUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiManagerDirectoryTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RootPath rootPath;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    @Autowired
    private FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    @Test
    public void getDirectories_success() throws Exception {
        String systemName = UUID.randomUUID().toString();

        final Path directoryPath = Path.of(rootPath.getRootPath(), systemName);
        Files.createDirectory(directoryPath);

        DirectoryRedis directoryRedis = new DirectoryRedis(
                UtilsAuthAction.LOGIN,
                UtilsAuthAction.ROLE_USER,
                systemName,
                "directory",
                directoryPath.toString(),
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/directory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataManagerDirectoryApi(
                                1,
                                List.of("directory"),
                                List.of(systemName)
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isOk());

        directoryRedisRepository.delete(directoryRedis);
        FileSystemUtils.deleteRecursively(directoryPath);
    }

    @Test
    public void getDirectories_success_forbidden() throws Exception {
        String systemName = UUID.randomUUID().toString();

        final Path directoryPath = Path.of(rootPath.getRootPath(), systemName);
        Files.createDirectory(directoryPath);

        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                UtilsAuthAction.ROLE_USER,
                "directory",
                systemName,
                directoryPath.toString(),
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis rightDirectory = new RightUserFileSystemObjectRedis(
                systemName + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW));
        rightUserFileSystemObjectRedisRepository.save(rightDirectory);
        directoryRedisRepository.save(directoryRedis);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/directory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataManagerDirectoryApi(
                                1,
                                List.of("directory"),
                                List.of(systemName)
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isOk());

        directoryRedisRepository.delete(directoryRedis);
        rightUserFileSystemObjectRedisRepository.delete(rightDirectory);
        FileSystemUtils.deleteRecursively(directoryPath);
    }

    @Test
    public void getDirectories_fail_isFrozenDirectories() throws Exception {
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                UtilsAuthAction.ROLE_USER,
                "directory",
                "directory",
                "directory",
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis rightDirectory = new RightUserFileSystemObjectRedis(
                "directory" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW));
        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                "directory");

        rightUserFileSystemObjectRedisRepository.save(rightDirectory);
        directoryRedisRepository.save(directoryRedis);
        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/directory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataManagerDirectoryApi(
                                1,
                                List.of("directory"),
                                List.of("directory")
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isBadRequest());

        directoryRedisRepository.delete(directoryRedis);
        rightUserFileSystemObjectRedisRepository.delete(rightDirectory);
        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);
    }

    @Test
    public void getDirectories_fail_invalidLevelDirectory() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/directory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataManagerDirectoryApi(
                                99,
                                List.of("directory"),
                                List.of("directory")
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isBadRequest());
    }

}
