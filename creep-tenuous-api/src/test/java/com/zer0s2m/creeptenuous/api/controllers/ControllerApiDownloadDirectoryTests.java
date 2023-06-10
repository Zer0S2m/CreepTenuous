package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectoryApi;
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
import org.springframework.util.FileSystemUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiDownloadDirectoryTests {
    Logger logger = LogManager.getLogger(ControllerApiDownloadDirectoryTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServiceBuildDirectoryPath buildDirectoryPath;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    List<String> DIRECTORIES_1 = List.of("test_folder1");

    DataDownloadDirectoryApi VALID_DATA_1 = new DataDownloadDirectoryApi(
            new ArrayList<>(),
            new ArrayList<>(),
            DIRECTORIES_1.get(0),
            DIRECTORIES_1.get(0)
    );

    DataDownloadDirectoryApi INVALID_DATA_1 = new DataDownloadDirectoryApi(
            new ArrayList<>(),
            new ArrayList<>(),
            null,
            null
    );

    DataDownloadDirectoryApi INVALID_DATA_2 = new DataDownloadDirectoryApi(
            null,
            null,
            DIRECTORIES_1.get(0),
            DIRECTORIES_1.get(0)
    );

    DataDownloadDirectoryApi INVALID_DATA_3 = new DataDownloadDirectoryApi(
            List.of("invalid", "path", "directory"),
            List.of("invalid", "path", "directory"),
            DIRECTORIES_1.get(0),
            DIRECTORIES_1.get(0)
    );

    @Test
    public void downloadDirectory_success() throws Exception {
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                buildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file1.txt"
        );
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                buildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file2.txt"
        );

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/download")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_DATA_1))
                        .header("Authorization",  accessToken)
        ).andExpect(status().isOk());

        Path directoryTest = Path.of(buildDirectoryPath.build(DIRECTORIES_1));
        FileSystemUtils.deleteRecursively(directoryTest);

        logger.info("Delete folder for tests: " + directoryTest);
    }

    @Test
    public void downloadDirectory_fail_notValidNameDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/download")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(INVALID_DATA_1))
                        .header("Authorization",  accessToken)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void downloadDirectory_fail_notValidParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/download")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(INVALID_DATA_2))
                        .header("Authorization",  accessToken)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void downloadDirectory_fail_invalidPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/download")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(INVALID_DATA_3))
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isNotFound());
    }
}
