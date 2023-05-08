package com.zer0s2m.CreepTenuous.api.controllers;

import com.zer0s2m.CreepTenuous.api.controllers.directory.copy.data.FormCopyDirectoryApi;
import com.zer0s2m.CreepTenuous.helpers.TestTagControllerApi;
import com.zer0s2m.CreepTenuous.helpers.UtilsActionForFiles;
import com.zer0s2m.CreepTenuous.helpers.UtilsAuthAction;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.directory.copy.enums.MethodCopyDirectory;
import com.zer0s2m.CreepTenuous.services.core.Directory;
import com.zer0s2m.CreepTenuous.services.directory.manager.exceptions.messages.ExceptionNotDirectoryMsg;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiCopyDirectoryTests {
    Logger logger = LogManager.getLogger(ControllerApiCreateDirectoryTests.class);

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
    public void copyDirectoryFolder_success() throws Exception {
        UtilsActionForFiles.createDirectories(DIRECTORIES_3, serviceBuildDirectoryPath, logger);
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file1.txt"
        );
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_2,
                "test_file2.txt"
        );

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(new FormCopyDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                List.of(DIRECTORIES_3.get(0)),
                                List.of(DIRECTORIES_3.get(0)),
                                DIRECTORIES_1.get(0),
                                DIRECTORIES_1.get(0),
                                MethodCopyDirectory.FOLDER.getMethod()
                        )))
                )
                .andExpect(status().isNoContent());

        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_1)));
        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_3)));
    }

    @Test
    public void copyDirectoryContent_success() throws Exception {
        UtilsActionForFiles.createDirectories(DIRECTORIES_3, serviceBuildDirectoryPath, logger);
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_1,
                "test_file1.txt"
        );
        UtilsActionForFiles.preparePreliminaryFilesForCopyDirectories(
                serviceBuildDirectoryPath,
                logger,
                DIRECTORIES_2,
                "test_file2.txt"
        );

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(new FormCopyDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                List.of(DIRECTORIES_3.get(0)),
                                List.of(DIRECTORIES_3.get(0)),
                                DIRECTORIES_1.get(0),
                                DIRECTORIES_1.get(0),
                                MethodCopyDirectory.CONTENT.getMethod()
                        )))
                )
                .andExpect(status().isNoContent());

        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_1)));
        FileSystemUtils.deleteRecursively(Path.of(serviceBuildDirectoryPath.build(DIRECTORIES_3)));
    }

    @Test
    public void copyDirectory_fail_invalidPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(new FormCopyDirectoryApi(
                                Arrays.asList("invalid", "path", "directory"),
                                Arrays.asList("invalid", "path", "directory"),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                "testFolder",
                                "testFolder",
                                1
                        )))
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
    public void copyDirectory_fail_invalidToPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(new FormCopyDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                Arrays.asList("invalid", "path", "directory"),
                                Arrays.asList("invalid", "path", "directory"),
                                "testFolder",
                                "testFolder",
                                1
                        )))
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
    public void copyDirectory_fail_notParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(new FormCopyDirectoryApi(
                                null,
                                null,
                                new ArrayList<>(),
                                new ArrayList<>(),
                                "testFolder",
                                "testFolder",
                                1
                        )))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void copyDirectory_fail_notToParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(new FormCopyDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                null,
                                null,
                                "testFolder",
                                "testFolder",
                                1
                        )))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void copyDirectory_fail_notFileName() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/directory/copy")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization",  accessToken)
                        .content(objectMapper.writeValueAsString(new FormCopyDirectoryApi(
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                "",
                                "",
                                1
                        )))
                )
                .andExpect(status().isBadRequest());
    }
}
