package com.zer0s2m.CreepTenuous.api.controllers;

import com.zer0s2m.CreepTenuous.helpers.TestTagControllerApi;
import com.zer0s2m.CreepTenuous.helpers.UtilsActionForFiles;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.directory.manager.exceptions.messages.ExceptionNotDirectoryMsg;
import com.zer0s2m.CreepTenuous.services.core.Directory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileSystemUtils;

import java.nio.file.Path;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    List<String> DIRECTORIES_1 = List.of("test_folder1");

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
                MockMvcRequestBuilders.get("/api/v1/directory/download")
                        .queryParam("parents", "")
                        .queryParam("directory", DIRECTORIES_1.get(0))
        ).andExpect(status().isOk());

        Path directoryTest = Path.of(buildDirectoryPath.build(DIRECTORIES_1));
        FileSystemUtils.deleteRecursively(directoryTest);

        logger.info("Delete folder for tests: " + directoryTest);
    }

    @Test
    public void downloadDirectory_fail_notValidNameDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/directory/download")
                        .queryParam("parents", "")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void downloadDirectory_fail_notValidParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/directory/download")
                        .queryParam("directory", DIRECTORIES_1.get(0))
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void downloadDirectory_fail_invalidPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/directory/download")
                        .queryParam("parents", "invalid", "path", "directory")
                        .queryParam("directory", DIRECTORIES_1.get(0))
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
}
