package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.data.DataMoveDirectoryApi;
import com.zer0s2m.creeptenuous.common.enums.MethodMoveDirectory;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagControllerApi;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiMoveDirectoryTests {
    Logger logger = LogManager.getLogger(ControllerApiMoveDirectoryTests.class);

    @Autowired
    private ServiceBuildDirectoryPath serviceBuildDirectoryPath;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    List<String> DIRECTORIES_1 = List.of("test_folder1");
    List<String> DIRECTORIES_2 = List.of("test_folder1", "test_folder2");
    List<String> DIRECTORIES_3 = List.of("test_folder3");

    @Test
    public void moveDirectoryFolder_success() throws Exception {
        UtilsActionForFiles.createDirectories(DIRECTORIES_3, serviceBuildDirectoryPath, logger);
        Path pathTestFile1 =  UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file1.txt"
        );
        Path pathTestFile2 = UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_2,
                "test_file2.txt"
        );

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/directory/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(new DataMoveDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                List.of(DIRECTORIES_3.get(0)),
                                List.of(DIRECTORIES_3.get(0)),
                                DIRECTORIES_1.get(0),
                                DIRECTORIES_1.get(0),
                                MethodMoveDirectory.FOLDER.getMethod()
                        )))
                )
                .andExpect(status().isNoContent());

        Path newPathTestFile1 = Path.of(
                serviceBuildDirectoryPath.build(DIRECTORIES_3),
                DIRECTORIES_1.get(0),
                "test_file1.txt"
        );
        Path newPathTestFile2 = Path.of(
                serviceBuildDirectoryPath.build(DIRECTORIES_3),
                DIRECTORIES_1.get(0),
                "test_folder2",
                "test_file2.txt"
        );
        Assertions.assertTrue(Files.exists(newPathTestFile1));
        Assertions.assertTrue(Files.exists(newPathTestFile2));
        Assertions.assertFalse(Files.exists(pathTestFile1));
        Assertions.assertFalse(Files.exists(pathTestFile2));

        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_3)));
    }

    @Test
    public void moveDirectoryContent_success() throws Exception {
        UtilsActionForFiles.createDirectories(DIRECTORIES_3, serviceBuildDirectoryPath, logger);
        Path pathTestFile1 =  UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file1.txt"
        );
        Path pathTestFile2 = UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_2,
                "test_file2.txt"
        );

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/directory/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(new DataMoveDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                List.of(DIRECTORIES_3.get(0)),
                                List.of(DIRECTORIES_3.get(0)),
                                DIRECTORIES_1.get(0),
                                DIRECTORIES_1.get(0),
                                MethodMoveDirectory.CONTENT.getMethod()
                        )))
                )
                .andExpect(status().isNoContent());

        Path newPathTestFile1 = Path.of(
                serviceBuildDirectoryPath.build(DIRECTORIES_3),
                "test_file1.txt"
        );
        Path newPathTestFile2 = Path.of(
                serviceBuildDirectoryPath.build(DIRECTORIES_3),
                "test_folder2",
                "test_file2.txt"
        );
        Assertions.assertTrue(Files.exists(newPathTestFile1));
        Assertions.assertTrue(Files.exists(newPathTestFile2));
        Assertions.assertFalse(Files.exists(pathTestFile1));
        Assertions.assertFalse(Files.exists(pathTestFile2));

        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_3)));
    }

    @Test
    public void moveDirectory_fail_invalidPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/directory/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(new DataMoveDirectoryApi(
                                Arrays.asList("invalid", "path", "directory"),
                                Arrays.asList("invalid", "path", "directory"),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                "testFolder",
                                "testFolder",
                                1
                        )))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void moveDirectory_fail_invalidToPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/directory/move")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(new DataMoveDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                Arrays.asList("invalid", "path", "directory"),
                                Arrays.asList("invalid", "path", "directory"),
                                "testFolder",
                                "testFolder",
                                1
                        )))
                )
                .andExpect(status().isNotFound());
    }
}
