package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.TestTagControllerApi;
import com.zer0s2m.creeptenuous.api.helpers.UtilsAuthAction;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.data.DataDeleteDirectoryApi;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiDeleteDirectoryTests {
    Logger logger = LogManager.getLogger(ControllerApiDeleteDirectoryTests.class);

    @Autowired
    private ServiceBuildDirectoryPath serviceBuildDirectoryPath;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    DataDeleteDirectoryApi RECORD_1 = new DataDeleteDirectoryApi(
            new ArrayList<>(),
            new ArrayList<>(),
            "test_folder1",
            "test_folder1"
    );

    DataDeleteDirectoryApi INVALID_RECORD = new DataDeleteDirectoryApi(
            Arrays.asList("invalid", "path", "directory"),
            Arrays.asList("invalid", "path", "directory"),
            "test_folder1",
            "test_folder1"
    );

    @Test
    public void deleteDirectory_success() throws Exception {
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
                        .header("Authorization",  accessToken)
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
                        .content(objectMapper.writeValueAsString(INVALID_RECORD))
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteDirectory_fail_notFileName() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/directory/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(new DataDeleteDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                "",
                                ""
                        )))
                )
                .andExpect(status().isBadRequest());
    }
}
