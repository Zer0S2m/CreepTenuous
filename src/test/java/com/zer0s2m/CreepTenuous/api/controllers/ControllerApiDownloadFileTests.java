package com.zer0s2m.CreepTenuous.api.controllers;

import com.zer0s2m.CreepTenuous.helpers.TestTagControllerApi;
import com.zer0s2m.CreepTenuous.helpers.UtilsActionForFiles;
import com.zer0s2m.CreepTenuous.api.controllers.common.exceptions.messages.NoSuchFileExists;
import com.zer0s2m.CreepTenuous.components.RootPath;
import com.zer0s2m.CreepTenuous.services.core.Directory;
import com.zer0s2m.CreepTenuous.services.directory.manager.exceptions.messages.ExceptionNotDirectoryMsg;
import com.zer0s2m.CreepTenuous.services.core.ExceptionFile;

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

    private final String nameTestFile1 = "test_image_1.jpeg";

    private final String failNameTestFile = "fail_name_test_file.fail_extension";

    @Test
    public void downloadFile_success() throws Exception {
        Path sourcePath = Path.of("src/main/resources/test/", nameTestFile1);
        Path targetPath = Path.of(rootPath.getRootPath(), nameTestFile1);
        Files.copy(sourcePath, targetPath);

        Assertions.assertTrue(Files.exists(targetPath));

        logger.info("Copy file: " + targetPath);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/v1/file/download")
                        .queryParam("filename", nameTestFile1)
                        .queryParam("parents", "")
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
                        .get("/api/v1/file/download")
                        .queryParam("filename", failNameTestFile)
                        .queryParam("parents", "")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new NoSuchFileExists(ExceptionFile.FILE_NOT_EXISTS.get())
                        )
                ));
    }

    @Test
    public void downloadFile_fail_invalidPathDirectory() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/v1/file/download")
                        .queryParam("filename", failNameTestFile)
                        .queryParam("parents", "invalid", "file", "directory")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new ExceptionNotDirectoryMsg(Directory.NOT_FOUND_DIRECTORY.get())
                        )
                ));
    }

    @Test
    public void downloadFile_fail_notValidNameFile() throws Exception {
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/file/download")
                                .queryParam("parents", "")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void downloadFile_fail_notValidParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/file/download")
                        .queryParam("filename", "testFile")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }
}
