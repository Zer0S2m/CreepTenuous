package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.api.helpers.TestTagControllerApi;
import com.zer0s2m.creeptenuous.api.helpers.UtilsAuthAction;
import com.zer0s2m.creeptenuous.api.helpers.UtilsActionForFiles;
import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.data.DataDeleteFileApi;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionNotDirectoryMsg;
import com.zer0s2m.creeptenuous.common.exceptions.messages.NoSuchFileExists;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiDeleteFileTests {
    Logger logger = LogManager.getLogger(ControllerApiDeleteFileTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    private RootPath rootPath;

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    private final String testFile1 = "testFile1.txt";
    private final String testFile2 = "testFile2.txt";

    DataDeleteFileApi RECORD_1 = new DataDeleteFileApi(testFile1, testFile1, new ArrayList<>(), new ArrayList<>());
    DataDeleteFileApi RECORD_2 = new DataDeleteFileApi(testFile2, testFile2, new ArrayList<>(), new ArrayList<>());
    DataDeleteFileApi INVALID_RECORD_PATH_DIRECTORY = new DataDeleteFileApi(
            "failTestFile",
            "failTestFile",
            Arrays.asList("invalid", "path", "directory"),
            Arrays.asList("invalid", "path", "directory")
    );
    DataDeleteFileApi INVALID_RECORD_NOT_EXISTS_FILE = new DataDeleteFileApi(
            "notExistsFileFail",
            "notExistsFileFail",
            new ArrayList<>(),
            new ArrayList<>()
    );

    @Test
    public void deleteFile_success() throws Exception {
        for (DataDeleteFileApi record : Arrays.asList(RECORD_1, RECORD_2)) {
            Path pathTestFile = UtilsActionForFiles.preparePreliminaryFiles(
                    record.fileName(), record.parents(), logger, buildDirectoryPath
            );
            Files.createFile(pathTestFile);

            this.mockMvc.perform(
                    MockMvcRequestBuilders.delete("/api/v1/file/delete")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(record))
                            .header("Authorization",  accessToken)
                    )
                    .andExpect(status().isNoContent());
            Assertions.assertFalse(Files.exists(pathTestFile));

            UtilsActionForFiles.deleteFileAndWriteLog(pathTestFile, logger);
        }
    }

    @Test
    public void deleteFile_file_invalidPathDirectory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(INVALID_RECORD_PATH_DIRECTORY))
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new ExceptionNotDirectoryMsg(Directory.NOT_FOUND_DIRECTORY.get())
                        )
                ));
    }

    @Test
    public void deleteFile_file_notIsExists() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(INVALID_RECORD_NOT_EXISTS_FILE))
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isNotFound())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new NoSuchFileExists(
                                        rootPath.getRootPath() +
                                        "/" +
                                        INVALID_RECORD_NOT_EXISTS_FILE.systemFileName())
                        )
                ));
    }

    @Test
    public void deleteFile_fail_notValidNameFile() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DataDeleteFileApi("", "", new ArrayList<>(), new ArrayList<>())
                        ))
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteFile_fail_notValidParents() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/file/delete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DataDeleteFileApi(
                                        "testFile",
                                        "testFile",
                                        null, null
                                )
                        ))
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isBadRequest());
    }
}
