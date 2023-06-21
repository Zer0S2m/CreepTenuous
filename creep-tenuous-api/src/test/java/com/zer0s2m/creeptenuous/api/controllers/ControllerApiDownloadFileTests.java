package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.data.DataDownloadFileApi;
import com.zer0s2m.creeptenuous.common.exceptions.messages.NoSuchFileExists;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
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

    @Autowired
    private RootPath rootPath;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    private final String nameTestFile1 = "test_image_1.jpeg";

    private final String failNameTestFile = "fail_name_test_file.fail_extension";

    DataDownloadFileApi VALID_DATA_1 = new DataDownloadFileApi(
            new ArrayList<>(),
            new ArrayList<>(),
            nameTestFile1,
            nameTestFile1
    );

    DataDownloadFileApi INVALID_DATA_1 = new DataDownloadFileApi(
            new ArrayList<>(),
            new ArrayList<>(),
            failNameTestFile,
            failNameTestFile
    );

    DataDownloadFileApi INVALID_DATA_2 = new DataDownloadFileApi(
            Arrays.asList("invalid", "path", "directory"),
            Arrays.asList("invalid", "path", "directory"),
            failNameTestFile,
            failNameTestFile
    );

    DataDownloadFileApi INVALID_DATA_3 = new DataDownloadFileApi(
            new ArrayList<>(),
            new ArrayList<>(),
            null,
            null
    );

    DataDownloadFileApi INVALID_DATA_4 = new DataDownloadFileApi(
            null,
            null,
            nameTestFile1,
            nameTestFile1
    );

    @Test
    public void downloadFile_success() throws Exception {
        Path sourcePath = Path.of("src/main/resources/test/", nameTestFile1);
        Path targetPath = Path.of(rootPath.getRootPath(), nameTestFile1);
        Files.copy(sourcePath, targetPath);

        Assertions.assertTrue(Files.exists(targetPath));

        logger.info("Copy file: " + targetPath);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/file/download")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_DATA_1))
                        .header("Authorization",  accessToken)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE));

        UtilsActionForFiles.deleteFileAndWriteLog(targetPath, logger);
        Assertions.assertFalse(Files.exists(targetPath));
    }

    @Test
    public void downloadFile_fail_notFoundFile() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/file/download")
                        .content(objectMapper.writeValueAsString(INVALID_DATA_1))
                        .header("Authorization",  accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new NoSuchFileExists(rootPath.getRootPath() + "/" + failNameTestFile)
                        )
                ));
    }

    @Test
    public void downloadFile_fail_invalidPathDirectory() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/v1/file/download")
                        .content(objectMapper.writeValueAsString(INVALID_DATA_2))
                        .header("Authorization",  accessToken)
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
                                .content(objectMapper.writeValueAsString(INVALID_DATA_3))
                                .header("Authorization",  accessToken)
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
                        .content(objectMapper.writeValueAsString(INVALID_DATA_4))
                        .header("Authorization",  accessToken)
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
                        .header("Authorization",  accessToken)
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
                        .header("Authorization",  accessToken)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());

        fileRedisRepository.delete(fileRedis);
    }

}
