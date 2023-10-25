package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.data.DataDownloadFileApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.models.FrozenFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
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
public class ControllerApiDownloadFileTests {

    Logger logger = LogManager.getLogger(ControllerApiDownloadFileTests.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private final RootPath rootPath = new RootPath();

    @Autowired
    private ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @Autowired
    private RightUserFileSystemObjectRedisRepository rightUserFileSystemObjectRedisRepository;

    @Autowired
    private FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    private final String nameTestFile1 = "test_image_1.jpeg";

    @Test
    public void downloadFile_success() throws Exception {
        Path sourcePath = Path.of("src/test/resources/", nameTestFile1);
        Path targetPath = Path.of(rootPath.getRootPath(), nameTestFile1);
        Files.copy(sourcePath, targetPath);

        Assertions.assertTrue(Files.exists(targetPath));

        logger.info("Copy file: " + targetPath);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/file/download")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataDownloadFileApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                nameTestFile1,
                                nameTestFile1
                        )))
                        .header("Authorization", accessToken)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE));

        UtilsActionForFiles.deleteFileAndWriteLog(targetPath, logger);
        Assertions.assertFalse(Files.exists(targetPath));
    }

    @Test
    public void downloadFile_fail_notFoundFile() throws Exception {
        String systemName = UUID.randomUUID().toString();

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/file/download")
                        .content(objectMapper.writeValueAsString(new DataDownloadFileApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                systemName,
                                systemName
                        )))
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void downloadFile_fail_invalidPathDirectory() throws Exception {
        String failNameTestFile = "fail_name_test_file.fail_extension";
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/file/download")
                        .content(objectMapper.writeValueAsString(new DataDownloadFileApi(
                                Arrays.asList("invalid", "path", "directory"),
                                Arrays.asList("invalid", "path", "directory"),
                                failNameTestFile,
                                failNameTestFile
                        )))
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void downloadFile_fail_notValidNameFile() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/file/download")
                        .content(objectMapper.writeValueAsString(new DataDownloadFileApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                null,
                                null
                        )))
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void downloadFile_fail_notValidParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/file/download")
                        .content(objectMapper.writeValueAsString(new DataDownloadFileApi(
                                null,
                                null,
                                nameTestFile1,
                                nameTestFile1
                        )))
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void downloadFile_fail_forbiddenDirectories() throws Exception {
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                "ROLE_USER",
                "testDirectory",
                "testDirectory",
                "testDirectory",
                new ArrayList<>());
        directoryRedisRepository.save(directoryRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/file/download")
                        .content(objectMapper.writeValueAsString(new DataDownloadFileApi(
                                List.of("testDirectory"),
                                List.of("testDirectory"),
                                nameTestFile1,
                                nameTestFile1
                        )))
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void downloadFile_fail_forbiddenFile() throws Exception {
        FileRedis fileRedis = new FileRedis(
                "login",
                "ROLE_USER",
                nameTestFile1,
                nameTestFile1,
                nameTestFile1,
                new ArrayList<>());
        fileRedisRepository.save(fileRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/file/download")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataDownloadFileApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                nameTestFile1,
                                nameTestFile1
                        )))
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());

        fileRedisRepository.delete(fileRedis);
    }

    @Test
    public void downloadFile_success_forbidden() throws Exception {
        final Path testDirectoryPath = Path.of(rootPath.getRootPath(), "testFolder1");
        Files.createDirectory(testDirectoryPath);
        Path sourcePath = Path.of("src/test/resources/", nameTestFile1);
        Path targetPath = Path.of(testDirectoryPath.toString(), nameTestFile1);
        Files.copy(sourcePath, targetPath);

        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                UtilsAuthAction.ROLE_USER,
                "testFolder1",
                "testFolder1",
                buildDirectoryPath.build(List.of("testFolder1")),
                List.of(UtilsAuthAction.LOGIN));
        FileRedis fileRedis = new FileRedis(
                "login",
                "ROLE_USER",
                nameTestFile1,
                nameTestFile1,
                nameTestFile1,
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis rightDirectory = new RightUserFileSystemObjectRedis(
                "testFolder1" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW));
        RightUserFileSystemObjectRedis rightFile = new RightUserFileSystemObjectRedis(
                nameTestFile1 + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.DOWNLOAD));
        fileRedisRepository.save(fileRedis);
        directoryRedisRepository.save(directoryRedis);
        rightUserFileSystemObjectRedisRepository.saveAll(List.of(rightDirectory, rightFile));

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/file/download")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataDownloadFileApi(
                                List.of("testFolder1"),
                                List.of("testFolder1"),
                                nameTestFile1,
                                nameTestFile1
                        )))
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        fileRedisRepository.delete(fileRedis);
        directoryRedisRepository.delete(directoryRedis);
        rightUserFileSystemObjectRedisRepository.deleteAll(List.of(rightDirectory, rightFile));

        FileSystemUtils.deleteRecursively(testDirectoryPath);
    }

    @Test
    public void downloadFile_fail_isFrozenDirectory() throws Exception {
        DirectoryRedis directoryRedis = new DirectoryRedis(
                "login",
                UtilsAuthAction.ROLE_USER,
                "testFolder1",
                "testFolder1",
                "testFolder1",
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis rightDirectory = new RightUserFileSystemObjectRedis(
                "testFolder1" + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW));
        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                "testFolder1");

        directoryRedisRepository.save(directoryRedis);
        rightUserFileSystemObjectRedisRepository.save(rightDirectory);
        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/file/download")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataDownloadFileApi(
                                List.of("testFolder1"),
                                List.of("testFolder1"),
                                nameTestFile1,
                                nameTestFile1
                        )))
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        directoryRedisRepository.delete(directoryRedis);
        rightUserFileSystemObjectRedisRepository.delete(rightDirectory);
        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);
    }

    @Test
    public void downloadFile_fail_isFrozenFail() throws Exception {
        FileRedis fileRedis = new FileRedis(
                "login",
                "ROLE_USER",
                nameTestFile1,
                nameTestFile1,
                nameTestFile1,
                List.of(UtilsAuthAction.LOGIN));
        RightUserFileSystemObjectRedis rightFile = new RightUserFileSystemObjectRedis(
                nameTestFile1 + "__" + UtilsAuthAction.LOGIN, UtilsAuthAction.LOGIN,
                List.of(OperationRights.SHOW, OperationRights.DOWNLOAD));
        FrozenFileSystemObjectRedis frozenFileSystemObjectRedis = new FrozenFileSystemObjectRedis(
                nameTestFile1);

        fileRedisRepository.save(fileRedis);
        rightUserFileSystemObjectRedisRepository.save(rightFile);
        frozenFileSystemObjectRedisRepository.save(frozenFileSystemObjectRedis);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/file/download")
                        .content(objectMapper.writeValueAsString(new DataDownloadFileApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                nameTestFile1,
                                nameTestFile1
                        )))
                        .header("Authorization", accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        fileRedisRepository.delete(fileRedis);
        rightUserFileSystemObjectRedisRepository.delete(rightFile);
        frozenFileSystemObjectRedisRepository.delete(frozenFileSystemObjectRedis);
    }

}
