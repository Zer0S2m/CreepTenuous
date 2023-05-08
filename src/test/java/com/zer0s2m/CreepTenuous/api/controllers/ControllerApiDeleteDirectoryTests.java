package com.zer0s2m.CreepTenuous.api.controllers;

import com.zer0s2m.CreepTenuous.api.controllers.directory.delete.data.FormDeleteDirectoryApi;
import com.zer0s2m.CreepTenuous.helpers.TestTagControllerApi;
import com.zer0s2m.CreepTenuous.helpers.UtilsActionForFiles;
import com.zer0s2m.CreepTenuous.helpers.UtilsAuthAction;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.core.Directory;
import com.zer0s2m.CreepTenuous.services.directory.manager.exceptions.messages.ExceptionNotDirectoryMsg;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    FormDeleteDirectoryApi RECORD_1 = new FormDeleteDirectoryApi(
            new ArrayList<>(),
            new ArrayList<>(),
            "test_folder1",
            "test_folder1"
    );

    FormDeleteDirectoryApi INVALID_RECORD = new FormDeleteDirectoryApi(
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
    public void deleteDirectory_fail_notFileName() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/directory/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(new FormDeleteDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                "",
                                ""
                        )))
                )
                .andExpect(status().isBadRequest());
    }
}
