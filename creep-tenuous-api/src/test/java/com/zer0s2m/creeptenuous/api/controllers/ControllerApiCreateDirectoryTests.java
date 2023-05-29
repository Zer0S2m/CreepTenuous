package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.TestTagControllerApi;
import com.zer0s2m.creeptenuous.api.helpers.UtilsAuthAction;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.data.DataCreateDirectoryApi;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionDirectoryExistsMsg;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionNotDirectoryMsg;
import com.zer0s2m.creeptenuous.common.http.ResponseCreateDirectoryApi;
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
import org.springframework.test.web.servlet.MvcResult;
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
public class ControllerApiCreateDirectoryTests {
    Logger logger = LogManager.getLogger(ControllerApiCreateDirectoryTests.class);

    @Autowired
    private ServiceBuildDirectoryPath serviceBuildDirectoryPath;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    List<String> DIRECTORIES_1 = List.of("test_folder1");

    DataCreateDirectoryApi RECORD_1 = new DataCreateDirectoryApi(
            new ArrayList<>(),
            new ArrayList<>(),
            "test_folder1"
    );

    DataCreateDirectoryApi INVALID_RECORD = new DataCreateDirectoryApi(
            Arrays.asList("invalid", "path", "directory"),
            Arrays.asList("invalid", "path", "directory"),
            "test_folder1"
    );

    @Test
    public void createDirectory_success() throws Exception {
        MvcResult result = this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
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

    /**
     * @deprecated
     */
    public void createDirectory_fail_directoryExists() throws Exception {
        UtilsActionForFiles.createDirectories(DIRECTORIES_1, serviceBuildDirectoryPath, logger);

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
                                new ExceptionDirectoryExistsMsg(
                                        Directory.DIRECTORY_EXISTS.get()
                                )
                        )
                ));

        Path newFolder = Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_1));
        UtilsActionForFiles.deleteFileAndWriteLog(newFolder, logger);
    }

    @Test
    public void createDirectory_fail_invalidPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(INVALID_RECORD))
                )
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new ExceptionNotDirectoryMsg(
                                        Directory.NOT_FOUND_DIRECTORY.get()
                                )
                        )
                ));
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
}
